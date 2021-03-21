package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;

public interface OtherUserDataService {

    ResponseEntity getOtherUserData(Long userId);
    ResponseEntity getOtherUserProfile(Long userId);

    ResponseEntity getListPersonalVerification(Long userId);
    ResponseEntity getListOtherUserCard(Long userId);

}
