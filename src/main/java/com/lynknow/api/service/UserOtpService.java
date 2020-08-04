package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface UserOtpService {

    ResponseEntity verifyWhatsapp();
    ResponseEntity verifyEmail();

    ResponseEntity challengeWhatsapp(String code);
    ResponseEntity challengeEmail(String code);

}
