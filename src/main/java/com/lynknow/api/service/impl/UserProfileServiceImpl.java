package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.UserProfile;
import com.lynknow.api.pojo.request.UserProfileRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.UserProfileRepository;
import com.lynknow.api.service.UserProfileService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity updateProfile(UserProfileRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());
            if (profile == null) {
                profile = new UserProfile();

                profile.setIsEmailVerified(0);
                profile.setIsWhatsappNoVerified(0);
                profile.setCreatedDate(new Date());
                profile.setIsActive(1);
            } else {
                profile.setUpdatedDate(new Date());
            }

            profile.setUserData(userSession);
            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setAddress1(request.getAddress1());
            profile.setAddress2(request.getAddress2());
            profile.setCountry(request.getCountry());
            profile.setWhatsappNo(request.getWhatsappNo());
            profile.setMobileNo(request.getMobileNo());

            userProfileRepo.save(profile);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());
            if (profile != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseProfile(profile)), HttpStatus.OK);
            } else {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
