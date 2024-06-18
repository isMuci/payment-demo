package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("t_order_info")
public class OrderInfo extends BaseEntity{
    private String title;
    private String orderNo;
    private Long userId;
    private Long productId;
    private Integer totalFee;
    private String codeUrl;
    private String orderStatus;
}
