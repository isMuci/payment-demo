package com.example.service;

import com.example.entity.PrePayRes;

public interface AliPayService {
    PrePayRes appPay(Long productId);
}
