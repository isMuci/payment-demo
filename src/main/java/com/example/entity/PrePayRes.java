package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrePayRes {
    private String orderNo;
    private String codeUrl;

    public PrePayRes(OrderInfo update) {
        orderNo=update.getOrderNo();
        codeUrl=update.getCodeUrl();
    }
}
