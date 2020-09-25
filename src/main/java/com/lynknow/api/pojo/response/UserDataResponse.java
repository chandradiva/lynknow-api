package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date joinDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date updatedDate;

    private int maxVerificationCredit = 0;
    private int currentVerificationCredit = 0;
    private String profilePhoto;
    private int maxTotalView = 500;
    private int usedTotalView = 0;
    private Date expiredPremiumDate;

}
