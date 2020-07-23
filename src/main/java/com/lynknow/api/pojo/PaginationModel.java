package com.lynknow.api.pojo;

import lombok.Data;

@Data
public class PaginationModel {

    private Integer page = 0;
    private Integer size = 10;
    private String sort = "asc";
    private String sortBy = "id";
    private String param = "";

    public PaginationModel() {
    }

    public PaginationModel(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public PaginationModel(Integer page, Integer size, String sort, String sortBy) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.sortBy = sortBy;
    }

    public PaginationModel(Integer page, Integer size, String sort, String sortBy, String param) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.sortBy = sortBy;
        this.param = param;
    }

}
