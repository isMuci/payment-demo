package com.example.controller;

import com.example.entity.OrderInfo;
import com.example.service.OrderInfoService;
import com.example.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order-info")
@Slf4j
public class OrderInfoController {

    @Autowired
    OrderInfoService orderInfoService;

    @GetMapping
    public Result<List<OrderInfo>> list(){
        List<OrderInfo> list=orderInfoService.listByCreateTimeDesc();
        return Result.ok(list);
    }

    @GetMapping("{orderNo}")
    public Result<OrderInfo> getByOrderNo(@PathVariable String orderNo){
        OrderInfo orderInfo = orderInfoService.getByOrderNo(OrderInfo.builder().orderNo(orderNo).build());
        return Result.ok(orderInfo);
    }

    @GetMapping("status/{orderNo}")
    public Result<String> status(@PathVariable String orderNo){
        OrderInfo orderInfo = orderInfoService.getByOrderNo(OrderInfo.builder().orderNo(orderNo).build());
        return Result.ok(orderInfo.getOrderStatus());
    }
}
