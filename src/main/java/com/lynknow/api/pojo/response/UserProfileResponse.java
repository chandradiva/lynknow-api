package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String country;
    private PhoneDetailResponse whatsappNo;
    private PhoneDetailResponse mobileNo;
    private String fbId;
    private String fbToken;
    private String fbEmail;
    private String googleId;
    private String googleToken;
    private String googleEmail;
    private int isWhatsappNoVerified = 0;
    private int isEmailVerified = 0;
    private String profilePhoto;
    private String city;
    private String postalCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date updatedDate;

}
