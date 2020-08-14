package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCardRequest {

    // 0 = reject
    // 1 = verify
    private Long cardId;
    private Integer itemId;
    private int verify = 0;
    private String reason;

}
