package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotifyUpdatedCardRequest {

    private List<Long> contactIds;
    private List<Long> userIds;

}
