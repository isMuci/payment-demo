package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.PaymentInfo;
import com.example.mapper.OrderInfoMapper;
import com.example.mapper.PaymentInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
}
