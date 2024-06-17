package com.example.controller;

import com.example.entity.OrderInfo;
import com.example.service.OrderInfoService;
import com.example.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order-info")
@Slf4j
public class OrderInfoController {

    @Autowired
    OrderInfoService orderInfoService;

    @GetMapping("list")
    public Result<List<OrderInfo>> list(){
        List<OrderInfo> list=orderInfoService.listByCreateTimeDesc();
        return Result.ok(list);
    }
}
