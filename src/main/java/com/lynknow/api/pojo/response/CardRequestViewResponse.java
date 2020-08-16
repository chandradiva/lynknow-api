package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CardRequestViewResponse {

    private Long id;
    private UserCardResponse userCard;
    private UserDataResponse userData;
    private int isGranted = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date expiredRequestDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date updatedDate;

    private int isActive;

}
