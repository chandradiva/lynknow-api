package com.lynknow.api.service;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.*;
import org.springframework.http.ResponseEntity;

public interface UserDataService {

    ResponseEntity registerAdmin(UserDataRequest request);
    ResponseEntity registerNewUser(UserDataRequest request);
    UserData getByUsername(String username);

    ResponseEntity registerFacebook(AuthSocialRequest request);
    ResponseEntity registerGoogle(AuthSocialRequest request);

    ResponseEntity forgotPassword(String email);
    ResponseEntity checkToken(String token);
    ResponseEntity resetPassword(ResetPasswordRequest request);

    ResponseEntity checkIsEmailVerified();
    ResponseEntity changeEmail(ChangeEmailRequest request);
    ResponseEntity verifyChangeEmail(String token);
    ResponseEntity getDetail(Long id);
    ResponseEntity changePassword(ChangePasswordRequest request);

    ResponseEntity updateExpiredTotalView();

    void resetToBasic(UserData user);

}
