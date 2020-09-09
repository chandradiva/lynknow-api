package com.lynknow.api.service;

import com.lynknow.api.model.UserCard;
import com.lynknow.api.pojo.PaginationModel;
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

    ResponseEntity generateOtpCompanyPhoneNumber(Long cardId);
    ResponseEntity challengeCompanyPhoneNumber(Long cardId, String code);

    ResponseEntity checkCredit();
    ResponseEntity getListNeedVerify(PaginationModel model);

}
