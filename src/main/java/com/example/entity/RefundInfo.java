package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("t_refund_info")
public class RefundInfo extends BaseEntity{

    private String orderNo;
    private String refundNo;
    private String refundId;
    private Integer totalFee;
    private Integer refund;
    private String reason;
    private String refundStatus;
    private String contentReturn;
    private String contentNotify;
}
