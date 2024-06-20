package com.example.config;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.util.model.AlipayConfig;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
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
    @PostConstruct
    public void aliPayClient() throws ApiException {
        System.out.println(appId);
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(gatewayUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(merchantPrivateKey);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        ApiClient defaultClient = com.alipay.v3.Configuration.getDefaultApiClient();
        // 初始化alipay参数（全局设置一次）
        defaultClient.setAlipayConfig(alipayConfig);

    }
}
