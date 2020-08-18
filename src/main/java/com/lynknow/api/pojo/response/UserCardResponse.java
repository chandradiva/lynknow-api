package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserCardResponse {

    private Long id;
    private UserDataResponse userData;
    private CardTypeResponse cardType;
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
    private PhoneDetailResponse whatsappNo;
    private PhoneDetailResponse mobileNo;
    private String fbEmail;
    private String googleEmail;
    private int isPublished = 0;
    private int isCardLocked = 0;
    private int isWhatsappNoVerified = 0;
    private int isEmailVerified = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date publishedDate;

    private String uniqueCode;
    private int verificationPoint = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date updatedDate;

}
