package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequest {

    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String country;
    private PhoneDetailRequest whatsappNo;
    private PhoneDetailRequest mobileNo;

}
