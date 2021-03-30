package com.lynknow.api.service;

import com.lynknow.api.model.UserCard;
import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

public interface UserContactService {

    ResponseEntity getListPaginationContact(PaginationModel model);
    ResponseEntity getListPaginationRequested(PaginationModel model);
    ResponseEntity getListPaginationReceived(PaginationModel model);
    ResponseEntity updateStatus(Long id, Integer status, Long cardId);

    void notifyUpdatedCard(UserCard updatedCard);

}
