package com.lynknow.api.pojo;

import lombok.Data;

import java.util.List;

@Data
public class MyPage<T> {

    private Integer currentPage;
    private Long total;
    private Integer perPage;
    private Integer lastPage;
    private String nextPageUrl;
    private String prevPageUrl;
    private Long from;
    private Long to;
    private List<T> data;

}
