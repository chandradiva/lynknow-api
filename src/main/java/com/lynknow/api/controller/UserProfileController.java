package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.UserProfileRequest;
import com.lynknow.api.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("")
    public ResponseEntity updateProfile(@RequestBody UserProfileRequest request) {
        return userProfileService.updateProfile(request);
    }

    @GetMapping("")
    public ResponseEntity getProfile() {
        return userProfileService.getProfile();
    }

}
