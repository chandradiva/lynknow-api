package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneDetailResponse {

    private Long id;
    private String countryCode;
    private String dialCode;
    private String number;

}
