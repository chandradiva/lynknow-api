package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubscriptionPackageRequest {

    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String currency;
    private String period;
    private Integer interval;
    private String imageUrl;
    private List<String> details;

}
