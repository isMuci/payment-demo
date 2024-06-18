package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.enums.PayType;
import com.wechat.pay.java.service.payments.model.Transaction;
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

    public PaymentInfo(Transaction t){
        orderNo=t.getOutTradeNo();
        transaction_id=t.getTransactionId();
        payment_type=PayType.WXPAY.getType();
        trade_type=t.getTradeType().toString();
        trade_state=t.getTradeState().toString();
        payer_total=t.getAmount().getPayerTotal();
        content=t.toString();
    }
}
