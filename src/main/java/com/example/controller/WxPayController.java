package com.example.controller;

import com.example.entity.PrePayRes;
import com.example.service.WxPayService;
import com.example.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return null;
    }
}
