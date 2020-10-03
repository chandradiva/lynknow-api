package com.lynknow.api.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSocialResponse {

    private int id;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
