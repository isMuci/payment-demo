package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.OrderInfo;

import java.util.List;

public interface OrderInfoService extends IService<OrderInfo> {
    OrderInfo createOrderByProductId(Long productId);
    OrderInfo update(OrderInfo orderInfo);
    List<OrderInfo> listByCreateTimeDesc();
}
