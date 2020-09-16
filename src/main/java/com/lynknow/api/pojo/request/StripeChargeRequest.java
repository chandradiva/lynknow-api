package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeChargeRequest {

    private Integer packageId;
    private Long chargeAmount;
    private String currency;
    private String chargeDesc;
    private String chargeToken;

}
