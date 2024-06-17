package com.example.service;

import com.example.entity.PrePayRes;
import jakarta.servlet.http.HttpServletRequest;

public interface WxPayService {
    PrePayRes appPay(Long productId);

    void appNotify(HttpServletRequest request);
}
