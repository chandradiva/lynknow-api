package com.lynknow.api.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserCardPublicResponse {

    private Long id;
    private UserDataPublicResponse userData;
    private CardTypeResponse cardType;
    private String frontSide;
    private String backSide;
    private String profilePhoto;
    private int isPublished = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date publishedDate;

    private String uniqueCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss Z", timezone = "GMT+7")
    private Date updatedDate;

}
