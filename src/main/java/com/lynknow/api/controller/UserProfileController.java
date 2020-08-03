package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.UserProfileRequest;
import com.lynknow.api.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @PostMapping("upload")
    public ResponseEntity uploadProfilePic(@RequestParam("file") MultipartFile file) {
        return userProfileService.uploadProfilePicture(file);
    }

    @GetMapping("get-pic")
    public byte[] getProfilePic(HttpServletResponse httpRes) throws IOException {
        return userProfileService.getProfilePhoto(httpRes);
    }

}
