package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryResponse {

    private Integer id;
    private String iso;
    private String name;
    private String niceName;
    private String iso3;
    private Integer numCode;
    private Integer phoneCode;

}
