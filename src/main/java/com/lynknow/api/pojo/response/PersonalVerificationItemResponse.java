package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalVerificationItemResponse {

    private Integer id;
    private String name;
    private String description;
    private int type = 0;

}
