package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("t_payment_info")
public class PaymentInfo extends BaseEntity{
    private String orderNo;
    private String transaction_id;
    private String payment_type;
    private String trade_type;
    private String trade_state;
    private Integer payer_total;
    private String content;
}
