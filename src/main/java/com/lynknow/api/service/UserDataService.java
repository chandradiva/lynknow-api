package com.lynknow.api.service;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.AuthSocialRequest;
import com.lynknow.api.pojo.request.UserDataRequest;
import org.springframework.http.ResponseEntity;

public interface UserDataService {

    ResponseEntity registerAdmin(UserDataRequest request);
    ResponseEntity registerNewUser(UserDataRequest request);
    UserData getByUsername(String username);

    ResponseEntity registerFacebook(AuthSocialRequest request);
    ResponseEntity registerGoogle(AuthSocialRequest request);

}
