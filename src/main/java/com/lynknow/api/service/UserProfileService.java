package com.lynknow.api.service;

import com.lynknow.api.pojo.request.AuthSocialRequest;
import com.lynknow.api.pojo.request.UserProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserProfileService {

    ResponseEntity updateProfile(UserProfileRequest request);
    ResponseEntity getProfile();
    ResponseEntity uploadProfilePicture(MultipartFile file);
    byte[] getProfilePhoto(HttpServletResponse httpResponse) throws IOException;

    ResponseEntity connectFacebook(AuthSocialRequest request);
    ResponseEntity connectGoogle(AuthSocialRequest request);

}
