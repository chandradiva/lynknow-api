package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCardRequest {

    private Long id;
    private Integer cardTypeId;
    private String frontSide;
    private String backSide;
    private String profilePhoto;
    private String firstName;
    private String lastName;
    private String designation;
    private String company;
    private String address1;
    private String address2;
    private String city;
    private String postalCode;
    private String country;
    private String email;
    private String website;
    private PhoneDetailRequest whatsappNo;
    private PhoneDetailRequest mobileNo;

}
