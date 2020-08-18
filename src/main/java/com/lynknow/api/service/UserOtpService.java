package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface UserOtpService {

    ResponseEntity verifyWhatsapp();
    ResponseEntity verifyEmail();

    ResponseEntity challengeWhatsapp(String code);
    ResponseEntity challengeEmail(String code);
    ResponseEntity peekOtp(String email, int type);

    ResponseEntity verifyCardWhatsapp(Long cardId);
    ResponseEntity verifyCardEmail(Long cardId);

    ResponseEntity challengeCardWhatsapp(Long cardId, String code);
    ResponseEntity challengeCardEmail(Long cardId, String code);
    ResponseEntity peekCardOtp(Long cardId, int type) throws Exception;

}
