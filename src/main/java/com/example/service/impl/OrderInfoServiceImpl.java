package com.example.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.Product;
import com.example.enums.OrderStatus;
import com.example.mapper.OrderInfoMapper;
import com.example.mapper.ProductMapper;
import com.example.service.OrderInfoService;
import com.example.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public OrderInfo createOrderByProductId(Long productId) {
        Product product = productMapper.selectById(productId);
        OrderInfo orderInfo = OrderInfo.builder()
                .productId(productId)
                .title(product.getTitle())
                .orderNo(OrderNoUtils.getOrderNo())
                .totalFee(product.getPrice())
                .orderStatus(OrderStatus.NOTPAY.getType())
                .build();
        baseMapper.insert(orderInfo);
        return orderInfo;
    }

    @Override
    public OrderInfo update(OrderInfo orderInfo) {
        baseMapper.update(orderInfo);
        return baseMapper.selectById(orderInfo.getProductId());
    }

    @Override
    public List<OrderInfo> listByCreateTimeDesc() {
        return baseMapper.listByCreateTimeDesc();
    }

    @Override
    public void updateByOrderNo(OrderInfo orderInfo) {
        baseMapper.updateByOrderNo(orderInfo);
    }

    @Override
    public OrderInfo getByOrderNo(OrderInfo orderInfo) {
        return baseMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo,orderInfo.getOrderNo()));
    }

    @Override
    public List<OrderInfo> getNoPayOrderByDuration(int i) {
        OrderInfo orderInfo = OrderInfo.builder().orderStatus(OrderStatus.NOTPAY.getType()).build();
        orderInfo.setCreateTime(DateUtil.offsetMinute(new Date(), -i).toJdkDate());
        return baseMapper.getNoPayOrderByDuration(orderInfo);
    }
}
