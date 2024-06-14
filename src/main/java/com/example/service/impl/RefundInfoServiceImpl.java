package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderInfo;
import com.example.entity.RefundInfo;
import com.example.mapper.OrderInfoMapper;
import com.example.mapper.RefundInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.RefundInfoService;
import org.springframework.stereotype.Service;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
}
