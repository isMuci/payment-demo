package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.PaymentInfo;
import com.example.enums.PayType;
import com.example.mapper.PaymentInfoMapper;
import com.example.service.PaymentInfoService;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
}
