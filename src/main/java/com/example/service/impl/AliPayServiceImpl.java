package com.example.service.impl;

import com.alipay.v3.ApiException;
import com.alipay.v3.util.GenericExecuteApi;
import com.example.entity.OrderInfo;
import com.example.entity.PrePayRes;
import com.example.service.AliPayService;
import com.example.service.OrderInfoService;
import com.example.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private OrderInfoService orderInfoService;


//    @Override
//    public PrePayRes appPay(Long productId) {
//        log.info("生成订单");
//        OrderInfo orderInfo=orderInfoService.createOrderByProductId(productId);
//        GenericExecuteApi api = new GenericExecuteApi();
//
//        // 构造请求参数以调用接口
//        Map<String, Object> bizParams = new HashMap<>();
//        Map<String, Object> bizContent = new HashMap<>();
//
//        // 设置订单标题
//        bizContent.put("subject", orderInfo.getTitle());
//
//        // 设置产品码
//        bizContent.put("product_code", "QUICK_MSECURITY_PAY");
//
//        // 设置商户订单号
//        bizContent.put("out_trade_no", OrderNoUtils.getOrderNo());
//        // 设置订单总金额
//        BigDecimal total = new BigDecimal(orderInfo.getTotalFee()).divide(new BigDecimal("100"));
//        bizContent.put("total_amount", total);
//
//        // 设置建议使用time_expire字段
//        bizContent.put("timeout_express", "90m");
//
//        bizParams.put("biz_content", bizContent);
//        String orderStr=null;
//        try {
//            // 如果是第三方代调用模式，请设置app_auth_token（应用授权令牌）
//            orderStr = api.sdkExecute("alipay.trade.app.pay", bizParams);
//            System.out.println(orderStr);
//        } catch (ApiException e) {
//            System.out.println("调用失败");
//        }
//        orderInfo.setCodeUrl(orderStr);
//        OrderInfo update = orderInfoService.update(orderInfo);
//        return new PrePayRes(update);
//    }
    @Override
    public PrePayRes appPay(Long productId) {
        log.info("生成订单");
        OrderInfo orderInfo=orderInfoService.createOrderByProductId(productId);
        GenericExecuteApi api = new GenericExecuteApi();

        // 构造请求参数以调用接口
        Map<String, Object> bizParams = new HashMap<>();
        Map<String, Object> bizContent = new HashMap<>();

        // 设置订单标题
        bizContent.put("subject", orderInfo.getTitle());

        // 设置产品码
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        // 设置商户订单号
        bizContent.put("out_trade_no", OrderNoUtils.getOrderNo());
        // 设置订单总金额
        BigDecimal total = new BigDecimal(orderInfo.getTotalFee()).divide(new BigDecimal("100"));
        bizContent.put("total_amount", orderInfo.getTotalFee());

        // 设置建议使用time_expire字段
//        bizContent.put("timeout_express", "90m");

        bizParams.put("biz_content", bizContent);
        String orderStr=null;
        try {
            // 如果是第三方代调用模式，请设置app_auth_token（应用授权令牌）
            orderStr = api.pageExecute("alipay.trade.page.pay","POST", bizParams);
            System.out.println(orderStr);
        } catch (ApiException e) {
            System.out.println("调用失败");
        }
        orderInfo.setCodeUrl(orderStr);
        OrderInfo update = orderInfoService.update(orderInfo);
        return new PrePayRes(update);
    }
}