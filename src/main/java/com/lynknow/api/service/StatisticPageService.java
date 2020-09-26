package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface StatisticPageService {

    ResponseEntity getStatistic(Long userId);

}
