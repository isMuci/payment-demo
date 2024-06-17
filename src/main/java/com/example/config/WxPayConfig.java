package com.example.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "wxpay")
@Data
public class WxPayConfig {
    private String mchId;
    private String mchSerialNo;
    private String privateKeyPath;
    private String apiV3Key;
    private String appid;
    private String domain;
    private String notifyDomain;
    private Config config;
    @PostConstruct
    public void WxPayConfig(){
        this.config=new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKeyFromPath(privateKeyPath)
                .merchantSerialNumber(mchSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }

    @Bean(name="wxPayConfig")
    public Config getConfig(){
        return this.config;
    }
}
