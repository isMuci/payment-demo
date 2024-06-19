package com.example.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.RefundInfo;
import com.example.enums.OrderStatus;
import com.example.enums.wxpay.WxRefundStatus;
import com.example.mapper.RefundInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.RefundInfoService;
import com.example.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public RefundInfo saveByOrderNo(RefundInfo refundInfo) {
        OrderInfo orderInfo = orderInfoService.getByOrderNo(OrderInfo.builder().orderNo(refundInfo.getOrderNo()).build());
        refundInfo.setRefundNo(OrderNoUtils.getRefundNo());
        refundInfo.setTotalFee(orderInfo.getTotalFee());
        refundInfo.setRefund(orderInfo.getTotalFee());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }

    @Override
    public List<RefundInfo> getProcessingRefundByDuration(int i) {
        RefundInfo refundInfo = RefundInfo.builder().refundStatus(WxRefundStatus.PROCESSING.getType()).build();
        refundInfo.setCreateTime(DateUtil.offsetMinute(new Date(), -i).toJdkDate());
        return baseMapper.getProcessingRefundByDuration(refundInfo);
    }

    @Override
    public void updateByRefundNo(RefundInfo refundInfo) {
        baseMapper.updateByRefundNo(refundInfo);
    }
}
