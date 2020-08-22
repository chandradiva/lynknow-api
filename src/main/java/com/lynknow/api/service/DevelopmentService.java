package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface DevelopmentService {

    ResponseEntity setToPremium();
    ResponseEntity setToBasic();

    ResponseEntity buyVerificationCredit();

}
