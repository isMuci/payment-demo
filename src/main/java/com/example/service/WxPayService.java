package com.example.service;

import com.example.entity.PrePayRes;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;

public interface WxPayService {
    PrePayRes appPay(Long productId);

    void appNotify(HttpServletRequest request);

    void cancelOrder(String orderNo);

    Transaction queryOrder(String orderNo);

    void checkOrderStatus(String orderNo);
}
