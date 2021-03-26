package com.lynknow.api.service;

import com.lynknow.api.model.UserCard;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CardViewersService {

    void saveData(UserCard cardSeen, UserData userSeenBy);
    ResponseEntity getListPagination(Long userId, PaginationModel model);
    void notifyUpdatedCard(UserCard updatedCard, List<Long> userIds);

}
