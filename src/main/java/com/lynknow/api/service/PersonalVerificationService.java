package com.lynknow.api.service;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.VerifyPersonalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PersonalVerificationService {

    void initVerification(UserData user);
    ResponseEntity initVerification();
    ResponseEntity requestToVerify(MultipartFile file, String remarks, Integer itemId);
    ResponseEntity verifyRequest(VerifyPersonalRequest request);
    ResponseEntity getDetail(Long id);
    ResponseEntity getDetail(Long userId, Integer itemId);
    ResponseEntity getList(Long userId);
    ResponseEntity getListNeedToVerify(PaginationModel model);

}
