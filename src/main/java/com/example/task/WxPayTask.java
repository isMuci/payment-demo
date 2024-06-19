package com.example.task;

import com.example.entity.OrderInfo;
import com.example.entity.RefundInfo;
import com.example.service.OrderInfoService;
import com.example.service.RefundInfoService;
import com.example.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WxPayTask {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RefundInfoService refundInfoService;

    @Autowired
    private WxPayService wxPayService;

//    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm(){
        log.info("查找超时订单");
        List<OrderInfo> list=orderInfoService.getNoPayOrderByDuration(5);
        for (OrderInfo orderInfo : list) {
            String orderNo = orderInfo.getOrderNo();
            log.warn("超时订单编号 : {}",orderNo);
            wxPayService.checkOrderStatus(orderNo);
        }
    }

//    @Scheduled(cron = "0/30 * * * * ?")
    public void refundConfirm(){
        log.info("查找超时退款");
        List<RefundInfo> list=refundInfoService.getProcessingRefundByDuration(5);
        for (RefundInfo refundInfo : list) {
            String refundNo = refundInfo.getRefundNo();
            log.warn("超时退款编号 : {}",refundNo);
            wxPayService.checkRefundStatus(refundNo);
        }
    }
}
