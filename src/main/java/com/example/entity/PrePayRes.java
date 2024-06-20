package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrePayRes {
    private String orderNo;
    private String formStr;

    public PrePayRes(OrderInfo o) {
        orderNo=o.getOrderNo();
        formStr =o.getCodeUrl();
    }
}
