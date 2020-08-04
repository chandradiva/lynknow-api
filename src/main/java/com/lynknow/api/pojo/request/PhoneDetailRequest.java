package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneDetailRequest {

    private String countryCode;
    private String dialCode;
    private String number;

}
