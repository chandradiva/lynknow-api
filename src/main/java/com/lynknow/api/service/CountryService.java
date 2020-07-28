package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface CountryService {

    ResponseEntity getList();
    ResponseEntity getDetail(Integer id);

}
