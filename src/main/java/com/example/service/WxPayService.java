package com.example.service;

import com.example.entity.BillReq;
import com.example.entity.PrePayRes;
import com.example.entity.RefundInfo;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface WxPayService {
    PrePayRes appPay(Long productId);

    void appNotify(HttpServletRequest request);

    void cancelOrder(String orderNo);

    Transaction queryOrder(String orderNo);

    void checkOrderStatus(String orderNo);

    void refund(RefundInfo refundInfo);

    Refund queryRefund(String refundNo);

    void checkRefundStatus(String refundNo);

    void refundNotify(HttpServletRequest request);

    String queryBill(BillReq billReq);

    String downloadBill(BillReq billReq) throws IOException;
}
