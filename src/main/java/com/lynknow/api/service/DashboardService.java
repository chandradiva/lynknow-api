package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface DashboardService {

    ResponseEntity getDataTotalUser();
    ResponseEntity getDataTotalCard();
    ResponseEntity getDataTotalCardVerification();
    ResponseEntity getDataTotalPersonalVerification();

}
