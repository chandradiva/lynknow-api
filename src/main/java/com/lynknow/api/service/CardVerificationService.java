package com.lynknow.api.service;

import com.lynknow.api.model.UserCard;
import com.lynknow.api.pojo.request.VerifyCardRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CardVerificationService {

    void initCardVerification(UserCard card);
    ResponseEntity initCardVerification(Long cardId);

    ResponseEntity requestToVerify(MultipartFile file, Long cardId, Integer itemId);
    ResponseEntity requestToVerify(String officePhone, Long cardId);

    ResponseEntity getDetail(Long id);
    ResponseEntity getDetail(Long cardId, Integer itemId);

    ResponseEntity getList(Long cardId);
    ResponseEntity verifyRequest(VerifyCardRequest request);

}
