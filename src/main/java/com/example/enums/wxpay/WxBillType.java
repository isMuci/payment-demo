package com.example.enums.wxpay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WxBillType {
    /**
     * 微信
     */
    TRADE_BILLS("tradebill"),

    /**
     * 申请资金账单
     */
    FUND_FLOW_BILLS("fundflowbill");


    /**
     * 类型
     */
    private final String type;
}