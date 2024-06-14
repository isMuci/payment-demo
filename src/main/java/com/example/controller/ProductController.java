package com.example.controller;

import com.example.config.WxPayConfig;
import com.example.entity.Product;
import com.example.service.ProductService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController<T> {

    @Autowired
    private ProductService productService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/test")
    public Result<String> test(){
        return Result.ok(wxPayConfig.getMchId());
    }

    @GetMapping("/list")
    public Result<List<Product>> list(){
        List<Product> list = productService.list();
        return Result.ok(list);
    }
}
