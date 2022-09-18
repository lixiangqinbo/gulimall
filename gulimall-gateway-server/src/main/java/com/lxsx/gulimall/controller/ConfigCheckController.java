package com.lxsx.gulimall.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigCheckController {
    @Value("${spring.application.name}")
    private String gateRoute;

    @GetMapping("/gate")
    public String getInfo(){
        return gateRoute;
    }
}
