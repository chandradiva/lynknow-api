package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface OtpTypeService {

    ResponseEntity getDetail(Integer id);
    ResponseEntity getList();

}
