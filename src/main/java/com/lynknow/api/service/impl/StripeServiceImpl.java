package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.pojo.request.StripeChargeRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public ResponseEntity createStripeCharge(StripeChargeRequest request) {
        try {
            Map<String, Object> chargeParams = new HashMap<>();

            chargeParams.put("amount", request.getChargeAmount());
            chargeParams.put("currency", request.getCurrency());
            chargeParams.put("description", request.getChargeDesc());
            chargeParams.put("source", request.getChargeToken());

            Charge charge = Charge.create(chargeParams);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    charge.getId()), HttpStatus.OK);
        } catch (InternalServerErrorException | StripeException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity retrieveCharge(String chargeId) {
        try {
            Charge charge = Charge.retrieve(chargeId);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    charge.getId()), HttpStatus.OK);
        } catch (InternalServerErrorException | StripeException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
