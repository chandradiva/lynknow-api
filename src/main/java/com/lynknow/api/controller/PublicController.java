package com.lynknow.api.controller;

import com.lynknow.api.service.CardVerificationService;
import com.lynknow.api.service.PersonalVerificationService;
import com.lynknow.api.service.UserCardService;
import com.lynknow.api.service.UserOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(path = "public")
public class PublicController {

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private UserOtpService userOtpService;

    @Autowired
    private CardVerificationService cardVerificationService;

    @Autowired
    private PersonalVerificationService personalVerificationService;

    @GetMapping("get-card")
    public ResponseEntity getCard(@RequestParam String code) {
        return userCardService.getDetailByCode(code);
    }

    @GetMapping(value = "get-image")
    public byte[] getImage(@RequestParam String filename, HttpServletResponse httpResponse) throws IOException {
        return userCardService.getImageCard(filename, httpResponse);
    }

    @GetMapping(value = "get-image-2", produces = {"image/png", "image/jpg"})
    public byte[] getImage2(@RequestParam String filename, HttpServletResponse httpResponse) throws IOException {
        return userCardService.getImageCard(filename, httpResponse);
    }

    @GetMapping(value = "get-data-card-verification", produces = {"image/*", "application/pdf"})
    public byte[] getDataCardVerification(@RequestParam String filename, HttpServletResponse httpResponse) throws IOException {
        return cardVerificationService.getData(filename, httpResponse);
    }

    @GetMapping(value = "get-data-personal-verification", produces = {"image/*", "application/pdf"})
    public byte[] getDataPersonalVerification(@RequestParam String filename, HttpServletResponse httpResponse) throws IOException {
        return personalVerificationService.getData(filename, httpResponse);
    }

    @GetMapping("peek-otp")
    public ResponseEntity peekOtp(
            @RequestParam String email,
            @RequestParam(defaultValue = "1") int type) {
        return userOtpService.peekOtp(email, type);
    }

    @GetMapping("peek-card-otp")
    public ResponseEntity peekCardOtp(
            @RequestParam Long cardId,
            @RequestParam(defaultValue = "1") int type
    ) throws Exception {
        return userOtpService.peekCardOtp(cardId, type);
    }

}
