package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.ResetPasswordRequest;
import com.lynknow.api.pojo.request.UserDataRequest;
import com.lynknow.api.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @PostMapping("admin-register")
    public ResponseEntity registerAdmin(@RequestBody UserDataRequest request) {
        return userDataService.registerAdmin(request);
    }

    @PostMapping("subs-register")
    public ResponseEntity registerNewUser(@RequestBody UserDataRequest request) {
        return userDataService.registerNewUser(request);
    }

    @PostMapping("forgot-password")
    public ResponseEntity forgotPassword(@RequestParam String email) {
        return userDataService.forgotPassword(email);
    }

    @GetMapping("check-token")
    public ResponseEntity checkToken(@RequestParam String token) {
        return userDataService.checkToken(token);
    }

    @PostMapping("reset-password")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request) {
        return userDataService.resetPassword(request);
    }

}
