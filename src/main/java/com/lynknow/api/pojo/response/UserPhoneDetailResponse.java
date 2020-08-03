package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPhoneDetailResponse {

    private Long id;
    private String countryCode;
    private String dialCode;
    private String number;

}
