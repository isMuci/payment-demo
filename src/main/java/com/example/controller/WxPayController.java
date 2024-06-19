package com.example.controller;

import com.example.entity.BillReq;
import com.example.entity.PaymentInfo;
import com.example.entity.PrePayRes;
import com.example.entity.RefundInfo;
import com.example.enums.wxpay.WxBillType;
import com.example.service.WxPayService;
import com.example.vo.Result;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("wx-pay")
@Slf4j
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    @PostMapping("app/{productId}")
    public Result<PrePayRes> appPay(@PathVariable Long productId){
        log.info("发起支付请求");
        PrePayRes prePayRes = wxPayService.appPay(productId);
        return Result.ok(prePayRes);
    }

    @PostMapping("app/notify")
    public Result appNotify(HttpServletRequest request){
        log.info("微信支付回调");
        wxPayService.appNotify(request);
        return Result.ok();
    }


    @GetMapping("cancel/{orderNo}")
    public Result cancel(@PathVariable String orderNo){
        log.info("取消订单");
        wxPayService.cancelOrder(orderNo);
        return Result.ok();
    }

    @GetMapping("query-order/{orderNo}")
    public Result<PaymentInfo> queryOrder(@PathVariable String orderNo){
        log.info("查询订单");
        Transaction transaction = wxPayService.queryOrder(orderNo);
        return Result.ok(new PaymentInfo(transaction));
    }

    @PostMapping("refund")
    public Result refund(@RequestBody RefundInfo refundInfo){
        log.info("申请退款");
        wxPayService.refund(refundInfo);
        return Result.ok();
    }

    @GetMapping("query-refund/{refundNo}")
    public Result<Refund> queryRefund(@PathVariable String refundNo){
        log.info("查询退款");
        Refund refund = wxPayService.queryRefund(refundNo);
        return Result.ok(refund);
    }

    @PostMapping("refund/notify")
    public Result refundNotify(HttpServletRequest request){
        log.info("微信退款回调");
        wxPayService.refundNotify(request);
        return Result.ok();
    }

    @PostMapping("bill/query")
    public Result<String> queryBill(@RequestBody BillReq billReq){
        log.info("申请微信账单");
        String downloadUrl=wxPayService.queryBill(billReq);
        return Result.ok(downloadUrl);
    }

    @PostMapping("bill/download")
    public Result<String> downloadBill(@RequestBody BillReq billReq) throws IOException {
        log.info("下载微信账单");
        String bill=wxPayService.downloadBill(billReq);
        return Result.ok(bill);
    }
}
