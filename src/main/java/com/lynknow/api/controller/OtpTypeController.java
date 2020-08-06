package com.lynknow.api.controller;

import com.lynknow.api.service.OtpTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "otp-types")
public class OtpTypeController {

    @Autowired
    private OtpTypeService otpTypeService;

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Integer id) {
        return otpTypeService.getDetail(id);
    }

    @GetMapping("")
    public ResponseEntity getList() {
        return otpTypeService.getList();
    }

}
