package com.example.controller;

import com.example.entity.PrePayRes;
import com.example.service.AliPayService;
import com.example.service.WxPayService;
import com.example.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ali-pay")
@Slf4j
public class AliPayController {
    @Autowired
    private AliPayService aliPayService;

    @PostMapping("app/{productId}")
    public Result<PrePayRes> appPay(@PathVariable Long productId){
        log.info("发起支付请求");
        PrePayRes prePayRes = aliPayService.appPay(productId);
        return Result.ok(prePayRes);
    }
}
