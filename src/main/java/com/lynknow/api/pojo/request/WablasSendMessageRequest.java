package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WablasSendMessageRequest {

    private String phone;
    private String message;

}
