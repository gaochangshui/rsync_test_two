package com.trechina.planocycle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(("/planoCycleApi/HelloWorld"))
public class HelloWorld {
    @GetMapping("/getHelloWorld")
    public String getHelloWorld(){
        return "cgiPath";
    }
}
