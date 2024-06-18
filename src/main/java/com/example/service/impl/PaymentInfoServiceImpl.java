package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.PaymentInfo;
import com.example.enums.PayType;
import com.example.mapper.OrderInfoMapper;
import com.example.mapper.PaymentInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.PaymentInfoService;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    @Override
    public void saveByTransaction(Transaction transaction) {
        baseMapper.insert(
                PaymentInfo.builder()
                        .orderNo(transaction.getOutTradeNo())
                        .transaction_id(transaction.getTransactionId())
                        .payment_type(PayType.WXPAY.getType())
                        .trade_type(transaction.getTradeType().toString())
                        .trade_state(transaction.getTradeState().toString())
                        .payer_total(transaction.getAmount().getPayerTotal())
                        .content(transaction.toString())
                        .build());
    }
}
