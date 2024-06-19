package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RefundInfo;

import java.util.List;

public interface RefundInfoService extends IService<RefundInfo> {
    RefundInfo saveByOrderNo(RefundInfo refundInfo);

    List<RefundInfo> getProcessingRefundByDuration(int i);

    void updateByRefundNo(RefundInfo refundInfo);
}
