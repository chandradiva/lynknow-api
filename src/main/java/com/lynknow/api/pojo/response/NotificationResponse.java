package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificationResponse {

    private Long id;
    private UserDataResponse userData;
    private UserDataResponse targetUserData;
    private UserCardResponse targetUserCard;
    private NotificationTypeResponse notificationType;
    private String remarks;
    private int isRead = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date updatedDate;

    private Long paramId;
    private Object additionalData;

}
