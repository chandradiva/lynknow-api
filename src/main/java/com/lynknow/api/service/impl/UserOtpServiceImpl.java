package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.ConflictException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.UserOtpService;
import com.lynknow.api.util.EmailUtil;
import com.lynknow.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class UserOtpServiceImpl implements UserOtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardTypeServiceImpl.class);

    @Autowired
    private UserOtpRepository userOtpRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private OtpTypeRepository otpTypeRepo;

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private UserPhoneDetailRepository userPhoneDetailRepo;

    @Autowired
    private EmailUtil emailUtil;

    @Value("${email.subject.send-otp}")
    private String subjectEmailOtp;

    @Override
    public ResponseEntity verifyWhatsapp() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());
            UserPhoneDetail phoneDetail = userPhoneDetailRepo.getDetail(profile.getId(), 1);

            if (profile.getIsWhatsappNoVerified() == 1) {
                LOGGER.error("Your WhatsApp Number is Already Verified");
                throw new ConflictException("Your WhatsApp Number is Already Verified");
            }

            OtpType type = otpTypeRepo.getDetail(1);
            if (type == null) {
                LOGGER.error("OTP Type ID: " + 1 + " is not found");
                throw new NotFoundException("OTP Type ID: " + 1);
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 3);

            UserOtp otp = new UserOtp();

            otp.setUserData(userLogin);
            otp.setOtpType(type);
            otp.setOtpCode(StringUtil.generateOtp());
            otp.setSendTo(phoneDetail.getDialCode() + phoneDetail.getNumber());
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyEmail() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());
            if (profile.getIsEmailVerified() == 1) {
                LOGGER.error("Your Email is Already Verified");
                throw new ConflictException("Your Email is Already Verified");
            }

            OtpType type = otpTypeRepo.getDetail(2);
            if (type == null) {
                LOGGER.error("OTP Type ID: " + 2 + " is not found");
                throw new NotFoundException("OTP Type ID: " + 2);
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 3);

            UserOtp otp = new UserOtp();

            otp.setUserData(userLogin);
            otp.setOtpType(type);
            otp.setOtpCode(StringUtil.generateOtp());
            otp.setSendTo(userLogin.getEmail());
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            // send email
            emailUtil.sendEmail(
                    otp.getSendTo(),
                    subjectEmailOtp,
                    "Your OTP Code is: " + otp.getOtpCode());
            // end of send email

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity challengeWhatsapp(String code) {
        try {
            Date today = new Date();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());
            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());

            Page<UserOtp> page = userOtpRepo.getDetail(
                    userLogin.getId(),
                    1, // whatsapp
                    code,
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                UserOtp otp = page.getContent().get(0);
                if (today.before(otp.getExpiredDate())) {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    profile.setIsWhatsappNoVerified(1);
                    profile.setUpdatedDate(new Date());

                    userProfileRepo.save(profile);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            null), HttpStatus.OK);
                } else {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    LOGGER.error("Your OTP Code is Already Expired");
                    throw new BadRequestException("Your OTP Code is Already Expired");
                }
            } else {
                LOGGER.error("OTP Code: " + code + " is not found");
                throw new NotFoundException("OTP Code: " + code + " is not found", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity challengeEmail(String code) {
        try {
            Date today = new Date();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());
            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());

            Page<UserOtp> page = userOtpRepo.getDetail(
                    userLogin.getId(),
                    2, // email
                    code,
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                UserOtp otp = page.getContent().get(0);
                if (today.before(otp.getExpiredDate())) {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    profile.setIsEmailVerified(1);
                    profile.setUpdatedDate(new Date());

                    userProfileRepo.save(profile);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            null), HttpStatus.OK);
                } else {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    LOGGER.error("Your OTP Code is Already Expired");
                    throw new BadRequestException("Your OTP Code is Already Expired");
                }
            } else {
                LOGGER.error("OTP Code: " + code + " is not found");
                throw new NotFoundException("OTP Code: " + code + " is not found", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }
    
}
