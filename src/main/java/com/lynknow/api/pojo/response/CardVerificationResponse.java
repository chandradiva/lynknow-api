package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CardVerificationResponse {

    private Long id;
    private UserCardResponse userCard;
    private CardVerificationItemResponse cardVerificationItem;
    private int isVerified = 0;
    private String param;
    private String reason;
    private int isRequested = 0;
    private Date createdDate;
    private Date updatedDate;
    private Date expiredDate;

}
