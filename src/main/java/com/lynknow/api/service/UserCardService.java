package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.UserCardRequest;
import org.springframework.http.ResponseEntity;

public interface UserCardService {

    ResponseEntity saveData(UserCardRequest request);
    ResponseEntity getList(Long userId, Integer typeId, Integer isPublished);
    ResponseEntity getListPagination(Long userId, Integer typeId, Integer isPublished, PaginationModel model);
    ResponseEntity getDetail(Long id);
    ResponseEntity deleteData(Long id);
    ResponseEntity publishCard(Long id, int type);

}
