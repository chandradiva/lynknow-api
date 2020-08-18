package com.lynknow.api.controller;

import com.lynknow.api.service.UserOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/otps")
public class UserOtpController {

    @Autowired
    private UserOtpService userOtpService;

    @GetMapping("verify-wa")
    public ResponseEntity verifyWhatsapp() {
        return userOtpService.verifyWhatsapp();
    }

    @GetMapping("verify-email")
    public ResponseEntity verifyEmail() {
        return userOtpService.verifyEmail();
    }

    @PatchMapping("challenge-wa")
    public ResponseEntity challengeWhatsapp(@RequestParam String code) {
        return userOtpService.challengeWhatsapp(code);
    }

    @PatchMapping("challenge-email")
    public ResponseEntity challengeEmail(@RequestParam String code) {
        return userOtpService.challengeEmail(code);
    }

    @GetMapping("verify-card-wa/{cardId}")
    public ResponseEntity verifyCardWhatsapp(@PathVariable Long cardId) {
        return userOtpService.verifyCardWhatsapp(cardId);
    }

    @GetMapping("verify-card-email/{cardId}")
    public ResponseEntity verifyCardEmail(@PathVariable Long cardId) {
        return userOtpService.verifyCardEmail(cardId);
    }

    @PatchMapping("challenge-card-wa")
    public ResponseEntity challengeCardWhatsapp(@RequestParam Long cardId, @RequestParam String code) {
        return userOtpService.challengeCardWhatsapp(cardId, code);
    }

    @PatchMapping("challenge-card-email")
    public ResponseEntity challengeCardEmail(@RequestParam Long cardId, @RequestParam String code) {
        return userOtpService.challengeCardEmail(cardId, code);
    }

}
