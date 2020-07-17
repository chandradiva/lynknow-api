package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface RoleDataService {

    ResponseEntity getList();
    ResponseEntity getListPagination(PaginationModel model);
    ResponseEntity getDetail(Integer id);

}
