package com.example.service.impl;

import cn.hutool.core.date.DateUtil;
import com.example.config.WxPayConfig;
import com.example.entity.*;
import com.example.enums.OrderStatus;
import com.example.enums.wxpay.*;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import com.example.service.RefundInfoService;
import com.example.service.WxPayService;
import com.example.util.HttpUtils;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.http.AbstractHttpClient;
import com.wechat.pay.java.core.http.DefaultHttpClientBuilder;
import com.wechat.pay.java.core.http.HttpClient;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.IOUtil;
import com.wechat.pay.java.service.billdownload.BillDownloadService;
import com.wechat.pay.java.service.billdownload.BillDownloadServiceExtension;
import com.wechat.pay.java.service.billdownload.model.GetFundFlowBillRequest;
import com.wechat.pay.java.service.billdownload.model.GetTradeBillRequest;
import com.wechat.pay.java.service.billdownload.model.QueryBillEntity;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
//TODO MUCI 2024/6/18: 为不同端的请求装配不同的service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RefundInfoService refundInfoService;

    private final ReentrantLock lock=new ReentrantLock();

    @Override
    public PrePayRes appPay(Long productId) {
        OrderInfo orderInfo=orderInfoService.createOrderByProductId(productId);
        log.info("appPay.orderInfo : {}",orderInfo);
        AppService service =new AppService.Builder().config(wxPayConfig.getConfig()).build();
        log.info("生成订单");
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(orderInfo.getTotalFee());
        request.setAmount(amount);
        request.setAppid(wxPayConfig.getAppId());
        request.setMchid(wxPayConfig.getMchId());
        request.setDescription(orderInfo.getTitle());
        request.setNotifyUrl(wxPayConfig.getNotifyDomain().concat(WxNotifyType.APP_NOTIFY.getType()));
        request.setOutTradeNo(orderInfo.getOrderNo());

        //TODO MUCI 2024/6/17: 全局异常处理，https://github.com/wechatpay-apiv3/wechatpay-java ，错误处理
        log.info("调用统一下单API");
        PrepayResponse response = service.prepay(request);
        log.info("appPay.response.prepayId : {}",response.getPrepayId());
        orderInfo.setCodeUrl(response.getPrepayId());
        OrderInfo update = orderInfoService.update(orderInfo);
        return new PrePayRes(update);
    }

    @Override
    public void appNotify(HttpServletRequest request) {
        log.info("解析微信支付回调请求数据");
        String wechatPaySerial = request.getHeader("wechatPaySerial");
        String wechatpayNonce = request.getHeader("wechatpayNonce");
        String wechatSignature = request.getHeader("wechatSignature");
        String wechatTimestamp  = request.getHeader("wechatTimestamp");
        String requestBody = HttpUtils.readData(request);
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();
        NotificationParser parser = new NotificationParser((NotificationConfig) wxPayConfig.getConfig());
        //TODO MUCI 2024/6/17: 全局异常处理，https://github.com/wechatpay-apiv3/wechatpay-java ，回调通知
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        log.info("处理订单");
        OrderInfo orderInfo = OrderInfo.builder().orderNo(transaction.getOutTradeNo()).build();
        //TODO MUCI 2024/6/18: 使用粒度更细的锁对单个订单上锁
        if(lock.tryLock()){
            try {
                OrderInfo resOrderInfo=orderInfoService.getByOrderNo(orderInfo);
                if(resOrderInfo!=null&&!OrderStatus.NOTPAY.getType().equals(resOrderInfo.getOrderStatus())){
                    return;
                }
                orderInfo.setOrderStatus(OrderStatus.SUCCESS.getType());
                orderInfoService.updateByOrderNo(orderInfo);
                paymentInfoService.save(new PaymentInfo(transaction));
            } finally {
                lock.unlock();
            }
        }
    }

    private void closeOrder(String orderNo){
        AppService service =new AppService.Builder().config(wxPayConfig.getConfig()).build();
        CloseOrderRequest closeRequest = new CloseOrderRequest();
        closeRequest.setMchid(wxPayConfig.getMchId());
        closeRequest.setOutTradeNo(orderNo);
        service.closeOrder(closeRequest);
    }

    @Override
    public void cancelOrder(String orderNo) {
        closeOrder(orderNo);
        OrderInfo orderInfo = OrderInfo.builder().orderNo(orderNo).orderStatus(OrderStatus.CANCEL.getType()).build();
        orderInfoService.updateByOrderNo(orderInfo);
    }

    @Override
    public Transaction queryOrder(String orderNo) {
        OrderInfo orderInfo = OrderInfo.builder().orderNo(orderNo).build();
        OrderInfo resOrderInfo = orderInfoService.getByOrderNo(orderInfo);
        AppService service =new AppService.Builder().config(wxPayConfig.getConfig()).build();
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(wxPayConfig.getMchId());
        queryRequest.setOutTradeNo(resOrderInfo.getOrderNo());
        Transaction result = null;
        try {
            result= service.queryOrderByOutTradeNo(queryRequest);
            System.out.println(result.getTradeState());
        } catch (ServiceException e) {
            log.info("code={}, message={}", e.getErrorCode(), e.getErrorMessage());
            log.info("reponse body={}", e.getResponseBody());
        }
        return result;
    }

    @Override
    public void checkOrderStatus(String orderNo) {
        log.warn("根据订单号核实订单状态 : {}", orderNo);
        Transaction result = this.queryOrder(orderNo);
        String tradeState = result.getTradeState().toString();

        OrderInfo orderInfo = OrderInfo.builder().orderNo(orderNo).build();
        if (WxTradeState.SUCCESS.getType().equals(tradeState)) {
            log.warn("核实订单已支付 : {}", orderNo);
            orderInfo.setOrderStatus(OrderStatus.SUCCESS.getType());
            orderInfoService.updateByOrderNo(orderInfo);
            paymentInfoService.save(new PaymentInfo((result)));
        }

        if (WxTradeState.NOTPAY.getType().equals(tradeState)) {
            log.warn("核实订单未支付 : {}", orderNo);
            closeOrder(orderNo);
            orderInfo.setOrderStatus(OrderStatus.CLOSED.getType());
            orderInfoService.updateByOrderNo(orderInfo);
        }
    }

    @Override
    public void refund(RefundInfo refundInfo) {
        RefundInfo resRefund=refundInfoService.saveByOrderNo(refundInfo);
        RefundService service=new RefundService.Builder().config(wxPayConfig.getConfig()).build();
        CreateRequest request=new CreateRequest();
        AmountReq amount=new AmountReq();
        amount.setRefund(resRefund.getRefund().longValue());
        amount.setTotal(resRefund.getTotalFee().longValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);
        request.setOutTradeNo(resRefund.getOrderNo());
        request.setOutRefundNo(resRefund.getRefundNo());
        request.setReason(resRefund.getReason());
        request.setNotifyUrl(wxPayConfig.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));

        Refund refund = service.create(request);
        orderInfoService.updateByOrderNo(OrderInfo.builder().orderNo(resRefund.getOrderNo()).orderStatus(OrderStatus.REFUND_PROCESSING.getType()).build());
        resRefund.setRefundId(refund.getRefundId());
        resRefund.setRefundStatus(refund.getStatus().toString());
        resRefund.setContentNotify(refund.toString());
        refundInfoService.updateById(resRefund);

    }

    @Override
    public Refund queryRefund(String refundNo) {
        RefundService service=new RefundService.Builder().config(wxPayConfig.getConfig()).build();
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setSubMchid(wxPayConfig.getMchId());
        request.setOutRefundNo(refundNo);
        Refund result = null;
        try {
            result= service.queryByOutRefundNo(request);
            System.out.println(result.getStatus());
        } catch (ServiceException e) {
            log.info("code={}, message={}", e.getErrorCode(), e.getErrorMessage());
            log.info("reponse body={}", e.getResponseBody());
        }
        return result;
    }

    @Override
    public void checkRefundStatus(String refundNo) {
        log.warn("根据退款号核实退款状态 : {}", refundNo);
        Refund result = this.queryRefund(refundNo);
        String tradeState = result.getStatus().toString();

        RefundInfo refundInfo = RefundInfo.builder().refundNo(refundNo).refundStatus(tradeState).build();
        if (!WxRefundStatus.PROCESSING.getType().equals(tradeState)) {
            log.warn("核实退款已处理 : {}", refundNo);
            refundInfo.setRefundStatus(tradeState);
            refundInfoService.updateByRefundNo(refundInfo);
        }
    }

    @Override
    public void refundNotify(HttpServletRequest request) {
        log.info("解析微信退款回调请求数据");
        String wechatPaySerial = request.getHeader("wechatPaySerial");
        String wechatpayNonce = request.getHeader("wechatpayNonce");
        String wechatSignature = request.getHeader("wechatSignature");
        String wechatTimestamp  = request.getHeader("wechatTimestamp");
        String requestBody = HttpUtils.readData(request);
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();
        NotificationParser parser = new NotificationParser((NotificationConfig) wxPayConfig.getConfig());
        //TODO MUCI 2024/6/17: 全局异常处理，https://github.com/wechatpay-apiv3/wechatpay-java ，回调通知
        Refund refund = parser.parse(requestParam, Refund.class);
        log.info("处理订单");
        OrderInfo orderInfo = OrderInfo.builder().orderNo(refund.getOutTradeNo()).build();
        //TODO MUCI 2024/6/18: 使用粒度更细的锁对单个订单上锁
        if(lock.tryLock()){
            try {
                OrderInfo resOrderInfo=orderInfoService.getByOrderNo(orderInfo);
                if(resOrderInfo!=null&&!OrderStatus.REFUND_PROCESSING.getType().equals(resOrderInfo.getOrderStatus())){
                    return;
                }
                orderInfo.setOrderStatus(OrderStatus.REFUND_SUCCESS.getType());
                orderInfoService.updateByOrderNo(orderInfo);
                RefundInfo refundInfo = RefundInfo.builder().refundNo(refund.getOutRefundNo()).refundStatus(refund.getStatus().toString()).build();
                refundInfoService.updateByRefundNo(refundInfo);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String queryBill(BillReq billReq) {
        BillDownloadService service = new BillDownloadService.Builder().config(wxPayConfig.getConfig()).build();
        String downloadUrl=null;
        if(WxBillType.TRADE_BILLS.getType().equals(billReq.getType())){
            GetTradeBillRequest request = new GetTradeBillRequest();
            request.setBillDate(DateUtil.format(billReq.getDate(),"yyyy-MM-DD"));
            QueryBillEntity tradeBill = service.getTradeBill(request);
            downloadUrl=tradeBill.getDownloadUrl();
        }else if(WxBillType.FUND_FLOW_BILLS.getType().equals(billReq.getType())){
            GetFundFlowBillRequest request = new GetFundFlowBillRequest();
            request.setBillDate(DateUtil.format(billReq.getDate(),"yyyy-MM-DD"));
            QueryBillEntity tradeBill = service.getFundFlowBill(request);
            downloadUrl=tradeBill.getDownloadUrl();
        }else{
            throw new RuntimeException("不支持的账单类型");
        }
        return downloadUrl;
    }

    //TODO MUCI 2024/6/19: 暂时无法测试，可能会由于验签问题导致无法接受账单，如出现这种情况，尝试改用wechatpay-apache-httpclient
    @Override
    public String downloadBill(BillReq billReq) throws IOException {
        String downloadUrl=queryBill(billReq);
        AbstractHttpClient httpClient = new DefaultHttpClientBuilder().config(wxPayConfig.getConfig()).build();
        InputStream inputStream = httpClient.download(downloadUrl);
        String respBody = IOUtil.toString(inputStream);
        inputStream.close();
        return respBody;
    }
}
