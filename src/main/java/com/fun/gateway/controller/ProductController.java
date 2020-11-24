package com.fun.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试路由和网关流控 Controller
 *
 * @author MrDJun 2020/10/26
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/list")
    public String list() {
        return "hello,world";
    }

}
