package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthSocialRequest {

    private String token;
    private String referralCode;
    
}
