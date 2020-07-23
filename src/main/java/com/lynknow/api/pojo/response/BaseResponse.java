package com.lynknow.api.pojo.response;

import lombok.*;

@Getter
@Setter
public class BaseResponse<Any> {

    private Boolean status = true;
    private Integer code = 200;
    private String message = "Success";
    private Any data;

    public BaseResponse(Boolean status, Integer code, String message, Any data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse() {
    }

}
