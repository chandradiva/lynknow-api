package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.*;
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

    @PostMapping("register-facebook")
    public ResponseEntity loginFacebook(@RequestBody AuthSocialRequest request) {
        return userDataService.registerFacebook(request, 0);
    }

    @PostMapping("register-google")
    public ResponseEntity loginGoogle(@RequestBody AuthSocialRequest request) {
        return userDataService.registerGoogle(request, 0);
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

    @GetMapping("check-is-email-verified")
    public ResponseEntity checkIsEmailVerified() {
        return userDataService.checkIsEmailVerified();
    }

    @PostMapping("request-change-email")
    public ResponseEntity changeEmail(@RequestBody ChangeEmailRequest request) {
        return userDataService.changeEmail(request);
    }

    @PostMapping("verify-change-email")
    public ResponseEntity verifyChangeEmail(@RequestParam String token) {
        return userDataService.verifyChangeEmail(token);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        return userDataService.getDetail(id);
    }

    @PostMapping("change-password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest request) {
        return userDataService.changePassword(request);
    }

    @PatchMapping("update-expired-total-view")
    public ResponseEntity updateExpiredTotalView() {
        return userDataService.updateExpiredTotalView();
    }

}
