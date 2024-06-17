package com.example.enums.wxpay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WxNotifyType {

	/**
	 * 支付通知
	 */
	NATIVE_NOTIFY("/api/wxpay/app/notify"),

	APP_NOTIFY("/wx-pay/app/notify"),
	/**
	 * 退款结果通知
	 */
	REFUND_NOTIFY("/wx-pay/refunds/notify");

	/**
	 * 类型
	 */
	private final String type;
}