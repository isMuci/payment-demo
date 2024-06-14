package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_product")
public class Product extends BaseEntity{
    private String title;
    private Integer price;
}
