package com.example.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.example.config.WxPayConfig;
import com.example.entity.OrderInfo;
import com.example.entity.PrePayRes;
import com.example.enums.OrderStatus;
import com.example.enums.wxpay.WxNotifyType;
import com.example.service.OrderInfoService;
import com.example.service.WxPayService;
import com.example.util.HttpUtils;
import com.example.util.OrderNoUtils;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.Amount;
import com.wechat.pay.java.service.payments.app.model.PrepayRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private OrderInfoService orderInfoService;

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
    }
}
