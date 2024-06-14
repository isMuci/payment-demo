package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_payment_info")
public class PaymentInfo extends BaseEntity{
    private String orderNo;
    private String transaction_id;
    private String payment_type;
    private String trade_type;
    private String trade_state;
    private Long payer_total;
    private String content;
}
