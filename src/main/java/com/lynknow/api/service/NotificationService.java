package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

public interface NotificationService {

    ResponseEntity getDetail(Long id);
    ResponseEntity getListPagination(Long userId, Long targetUserId, Integer typeId, Integer isRead, PaginationModel model);
    ResponseEntity markAsRead(Long id);
    ResponseEntity deleteData(Long id);

}
