package com.lynknow.api.service;

import com.lynknow.api.pojo.request.StripeChargeRequest;
import org.springframework.http.ResponseEntity;

public interface StripeService {

    ResponseEntity createStripeCharge(StripeChargeRequest request);
    ResponseEntity retrieveCharge(String chargeId);

}
