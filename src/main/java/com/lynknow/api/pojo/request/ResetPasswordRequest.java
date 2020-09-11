package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    private String accessToken;
    private String password;
    private String confirmPassword;

}
