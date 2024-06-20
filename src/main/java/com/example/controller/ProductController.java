package com.example.controller;

import com.example.config.WxPayConfig;
import com.example.entity.Product;
import com.example.service.ProductService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/list")
    public Result<Map<String,List<Product>>> list(){
        List<Product> list = productService.list();
        Map<String,List<Product>> map=new HashMap<>();
        map.put("productList",list);
        return Result.ok(map);
    }
}
