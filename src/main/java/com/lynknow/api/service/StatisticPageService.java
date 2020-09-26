package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

public interface StatisticPageService {

    ResponseEntity getStatistic(Long userId);
    ResponseEntity getListDetail(Long userId, Integer typeId, PaginationModel model);

}
