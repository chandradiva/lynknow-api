package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

public interface StatisticPageService {

    ResponseEntity getStatistic(Long userId, Long cardId);
    ResponseEntity getListDetail(Long userId, Long cardId, Integer typeId, PaginationModel model);

}
