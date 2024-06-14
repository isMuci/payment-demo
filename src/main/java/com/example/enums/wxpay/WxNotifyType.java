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
	/**
	 * 退款结果通知
	 */
	REFUND_NOTIFY("/api/wxpay/refunds/notify");

	/**
	 * 类型
	 */
	private final String type;
}