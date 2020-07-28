package com.lynknow.api.service;

import com.lynknow.api.pojo.request.UserProfileRequest;
import org.springframework.http.ResponseEntity;

public interface UserProfileService {

    ResponseEntity updateProfile(UserProfileRequest request);
    ResponseEntity getProfile();

}
