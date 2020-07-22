package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface CardTypeService {

    ResponseEntity getDetail(Integer id);
    ResponseEntity getList();

}
