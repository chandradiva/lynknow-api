package com.lynknow.api.controller;

import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.RoleDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @Autowired
    private RoleDataRepository roleDataRepo;

    @GetMapping("check")
    public ResponseEntity testApi() {
        return new ResponseEntity(BaseResponse.ok(), HttpStatus.OK);
    }

    @GetMapping("check-db")
    public ResponseEntity testDb() {
        return new ResponseEntity(BaseResponse.ok(roleDataRepo.getList()), HttpStatus.OK);
    }

}
