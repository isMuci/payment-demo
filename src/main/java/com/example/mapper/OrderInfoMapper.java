package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    int update(OrderInfo orderInfo);

    List<OrderInfo> listByCreateTimeDesc();
}
