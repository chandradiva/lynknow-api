package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticPageResponse {

    private Integer id;
    private String name;
    private int stats = 0;

}
