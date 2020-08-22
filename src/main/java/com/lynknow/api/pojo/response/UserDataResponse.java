package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDataResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int verificationPoint = 0;
    private SubscriptionPackageResponse currentSubscription;
    private RoleDataResponse role;
    private UserProfileResponse profile;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date joinDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date updatedDate;

    private int maxVerificationCredit = 0;
    private int currentVerificationCredit = 0;

}
