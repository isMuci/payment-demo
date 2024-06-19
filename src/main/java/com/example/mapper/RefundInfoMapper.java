package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.RefundInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RefundInfoMapper extends BaseMapper<RefundInfo> {
    List<RefundInfo> getProcessingRefundByDuration(RefundInfo refundInfo);

    void updateByRefundNo(RefundInfo refundInfo);
}
