package com.lynknow.api.controller;

import com.lynknow.api.pojo.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @GetMapping("check")
    public BaseResponse testApi() {
        return BaseResponse.ok();
    }

}
