package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.UserDataRequest;
import com.lynknow.api.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @PostMapping("admin-register")
    public ResponseEntity registerAdmin(@RequestBody UserDataRequest request) {
        return userDataService.registerAdmin(request);
    }

}
