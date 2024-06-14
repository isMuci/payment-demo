package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_refund_info")
public class RefundInfo extends BaseEntity{

    private String orderNo;
    private String refund_no;
    private String refund_id;
    private Long total_fee;
    private Long refund;
    private String reason;
    private String refund_status;
    private String content_return;
    private String content_notify;
}
