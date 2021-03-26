package com.lynknow.api.controller;

import com.lynknow.api.service.OtherUserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "other-users")
public class OtherUserDataController {

    @Autowired
    private OtherUserDataService otherUserDataService;

    @GetMapping("get-user")
    public ResponseEntity getOtherUser(@RequestParam Long userId) {
        return otherUserDataService.getOtherUserData(userId);
    }

    @GetMapping("get-profile")
    public ResponseEntity getOtherProfile(@RequestParam Long userId) {
        return otherUserDataService.getOtherUserProfile(userId);
    }

    @GetMapping("list-verification")
    public ResponseEntity getListPersonalVerification(@RequestParam Long userId) {
        return otherUserDataService.getListPersonalVerification(userId);
    }

    @GetMapping("list-card")
    public ResponseEntity getListCard(@RequestParam Long userId) {
        return otherUserDataService.getListOtherUserCard(userId);
    }

}
