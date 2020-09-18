package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MarkAsReadBulkRequest {

    private List<Long> ids;

}
