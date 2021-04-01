package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.PersonalVerification;
import com.lynknow.api.model.UserCard;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.UserProfile;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.PersonalVerificationResponse;
import com.lynknow.api.pojo.response.UserCardResponse;
import com.lynknow.api.repository.PersonalVerificationRepository;
import com.lynknow.api.repository.UserCardRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.repository.UserProfileRepository;
import com.lynknow.api.service.CardViewersService;
import com.lynknow.api.service.OtherUserDataService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtherUserDataServiceImpl implements OtherUserDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardTypeServiceImpl.class);

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private PersonalVerificationRepository personalVerificationRepo;

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private CardViewersService cardViewersService;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getOtherUserData(Long userId) {
        try {
            UserData user = userDataRepo.getDetail(userId);
            if (user == null) {
                LOGGER.error("Other User Data ID: " + userId + " is not found");
                throw new NotFoundException("Other User Data ID: " + userId);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseUserComplete(user)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getOtherUserProfile(Long userId) {
        try {
            UserProfile profile = userProfileRepo.getDetailByUserId(userId);
            if (profile == null) {
                LOGGER.error("Other User Profile ID: " + userId + " is not found");
                throw new NotFoundException("Other User Profile ID: " + userId);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPersonalVerification(Long userId) {
        try {
            List<PersonalVerificationResponse> res = new ArrayList<>();
            List<PersonalVerification> verifications = personalVerificationRepo.getList(userId);
            if (verifications != null) {
                for (PersonalVerification item : verifications) {
                    res.add(generateRes.generateResponsePersonalVerification(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    res), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListOtherUserCard(Long userId) {
        try {
            List<UserCardResponse> res = new ArrayList<>();
            List<UserCard> cards = userCardRepo.getList(userId, null, 1, Sort.by("id").ascending());
            if (cards != null) {
                for (UserCard item : cards) {
                    res.add(generateRes.generateResponseUserCard(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    res), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
