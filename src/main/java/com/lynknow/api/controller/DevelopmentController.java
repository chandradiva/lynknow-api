package com.lynknow.api.controller;

import com.lynknow.api.service.DevelopmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "developments")
public class DevelopmentController {

    @Autowired
    private DevelopmentService developmentService;

    @PostMapping("set-premium")
    public ResponseEntity setToPremium() {
        return developmentService.setToPremium();
    }

    @PostMapping("set-basic")
    public ResponseEntity setToBasic() {
        return developmentService.setToBasic();
    }

    @PostMapping("buy-verification-credit")
    public ResponseEntity buyVerificationCredit() {
        return developmentService.buyVerificationCredit();
    }

}
