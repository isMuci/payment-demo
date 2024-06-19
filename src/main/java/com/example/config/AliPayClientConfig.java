package com.example.config;

import com.alipay.api.*;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alpay")
@Data
public class AliPayClientConfig {
    private String appId;
    private String sellerId;
    private String gatewayUrl;
    private String merchantPrivateKey;
    private String alipayPublicKey;
    private String contentKey;
    private String returnUrl;
    private String notifyUrl;
    private AlipayClient client;
    @PostConstruct
    public void aliPayClientConfig() throws AlipayApiException {

        AlipayConfig alipayConfig = new AlipayConfig();
        //设置网关地址
        alipayConfig.setServerUrl(gatewayUrl);
        //设置应用ID
        alipayConfig.setAppId(appId);
        //设置应用私钥
        alipayConfig.setPrivateKey(merchantPrivateKey);
        //设置请求格式，固定值json
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        //设置字符集
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        //设置支付宝公钥
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        //设置签名类型
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
        //构造client
        client= new DefaultAlipayClient(alipayConfig);
    }
}
