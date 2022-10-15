package com.lxsx.gulimall.controller;


import com.aliyun.oss.OSSClient;

import com.lxsx.gulimall.service.OssServer;
import com.lxsx.gulimall.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@RestController
public class AliyunOssController {


    @Resource
    private OSSClient ossClient;

    @Autowired
    @Qualifier("ossServer")
    private OssServer ossServer;

    @GetMapping("/upload")
    public R upload(){

        return R.ok().put("data", ossServer.requestSignature());
    }
}
