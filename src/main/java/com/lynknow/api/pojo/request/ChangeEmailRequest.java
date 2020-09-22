package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {

    private String oldEmail;
    private String newEmail;

}
