package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.StripeChargeRequest;
import com.lynknow.api.service.DevelopmentService;
import com.lynknow.api.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "developments")
public class DevelopmentController {

    @Autowired
    private DevelopmentService developmentService;

    @Autowired
    private StripeService stripeService;

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

    @PostMapping("test-charge")
    public ResponseEntity testCharge(@RequestBody StripeChargeRequest request) {
        return stripeService.createStripeCharge(request);
    }

    @GetMapping("retrieve-charge")
    public ResponseEntity retrieveCharge(@RequestParam String chargeId) {
        return stripeService.retrieveCharge(chargeId);
    }

}
