package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.SubscriptionPackageRequest;
import org.springframework.http.ResponseEntity;

public interface SubscriptionPackageService {

    ResponseEntity saveData(SubscriptionPackageRequest request);
    ResponseEntity getDetail(Integer id);
    ResponseEntity getList(PaginationModel model);
    ResponseEntity deleteData(Integer id);

}
