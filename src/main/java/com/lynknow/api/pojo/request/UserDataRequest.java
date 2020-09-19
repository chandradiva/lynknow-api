package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDataRequest {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String referralCode;

}
