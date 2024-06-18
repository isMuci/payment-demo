package com.example.controller;

import com.example.entity.PaymentInfo;
import com.example.entity.PrePayRes;
import com.example.service.WxPayService;
import com.example.vo.Result;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("query/{orderNo}")
    public Result<PaymentInfo> query(@PathVariable String orderNo){
        log.info("查询订单");
        Transaction transaction = wxPayService.queryOrder(orderNo);
        return Result.ok(new PaymentInfo(transaction));
    }
}
