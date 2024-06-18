package com.example.service.impl;

import com.example.config.WxPayConfig;
import com.example.entity.OrderInfo;
import com.example.entity.PaymentInfo;
import com.example.entity.PrePayRes;
import com.example.enums.OrderStatus;
import com.example.enums.wxpay.WxNotifyType;
import com.example.enums.wxpay.WxTradeState;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import com.example.service.WxPayService;
import com.example.util.HttpUtils;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        request.setAppid(wxPayConfig.getAppid());
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
}
