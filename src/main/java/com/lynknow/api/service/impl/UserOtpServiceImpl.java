package com.lynknow.api.service.impl;

import com.lynknow.api.exception.*;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.request.WablasSendMessageRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.UserOtpService;
import com.lynknow.api.util.EmailUtil;
import com.lynknow.api.util.HttpRequestUtil;
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

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private CardPhoneDetailRepository cardPhoneDetailRepo;

    @Value("${email.subject.send-otp}")
    private String subjectEmailOtp;

    @Value("${email.subject.send-otp-card}")
    private String subjectEmailOtpCard;

    @Value("${wablas.message.send-otp}")
    private String wablasSendOtp;

    @Value("${wablas.message.send-otp-card}")
    private String wablasSendOtpCard;

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

            UserOtp otp;
            Page<UserOtp> page = userOtpRepo.getDetail(
                    userLogin.getId(),
                    1, // whatsapp
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                otp = page.getContent().get(0);
                otp.setUpdatedDate(new Date());
            } else {
                otp = new UserOtp();
            }

            otp.setUserData(userLogin);
            otp.setOtpType(type);
            otp.setOtpCode(StringUtil.generateOtp());
            otp.setSendTo(phoneDetail.getDialCode() + StringUtil.normalizePhoneNumber(phoneDetail.getNumber()));
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            // send whatsapp
            WablasSendMessageRequest request = new WablasSendMessageRequest();

            request.setPhone(StringUtil.normalizePhoneNumber(otp.getSendTo()));
            request.setMessage(wablasSendOtp.replace("#", otp.getOtpCode()));

            new Thread(() -> {
                try {
                    httpRequestUtil.sendPost(request);
                } catch (Exception e) {
                    LOGGER.error("Error processing data", e);
                }
            }).start();
            // end of send whatsapp

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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

            UserOtp otp;
            Page<UserOtp> page = userOtpRepo.getDetail(
                    userLogin.getId(),
                    2, // email
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                otp = page.getContent().get(0);
                otp.setUpdatedDate(new Date());
            } else {
                otp = new UserOtp();
            }

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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

                    // update verification point
                    userLogin.setVerificationPoint(userLogin.getVerificationPoint() + 20);
                    userLogin.setUpdatedDate(new Date());

                    userDataRepo.save(userLogin);
                    // end of update verification point

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
                LOGGER.error("Your OTP Code: " + code + " is invalid");
                throw new NotFoundException("OTP Code: " + code + " is invalid", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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

                    // update verification point
                    userLogin.setVerificationPoint(userLogin.getVerificationPoint() + 20);
                    userLogin.setUpdatedDate(new Date());

                    userDataRepo.save(userLogin);
                    // end of update verification point

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
                LOGGER.error("OTP Code: " + code + " is invalid");
                throw new NotFoundException("OTP Code: " + code + " is invalid", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity peekOtp(String email, int type) {
        try {
            UserData user = null;
            Page<UserData> pageUser = userDataRepo.getByEmail(
                    email.toLowerCase(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                user = pageUser.getContent().get(0);

                Page<UserOtp> pageOtp = userOtpRepo.getDetail(
                        user.getId(),
                        type,
                        PageRequest.of(0, 1, Sort.by("id").descending()));
                if (pageOtp.getContent() != null && pageOtp.getContent().size() > 0) {
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            pageOtp.getContent().get(0).getOtpCode()), HttpStatus.OK);
                } else {
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            null), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyCardWhatsapp(Long cardId) {
        try {
            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            CardPhoneDetail phoneDetail = cardPhoneDetailRepo.getDetail(card.getId(), 1);

            if (card.getIsWhatsappNoVerified() == 1) {
                LOGGER.error("Your Card WhatsApp Number is Already Verified");
                throw new ConflictException("Your Card WhatsApp Number is Already Verified");
            }

            OtpType type = otpTypeRepo.getDetail(1);
            if (type == null) {
                LOGGER.error("OTP Type ID: " + 1 + " is not found");
                throw new NotFoundException("OTP Type ID: " + 1);
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 3);

            UserOtp otp;
            Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    1, // whatsapp
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                otp = page.getContent().get(0);
                otp.setUpdatedDate(new Date());
            } else {
                otp = new UserOtp();
            }

            otp.setUserCard(card);
            otp.setOtpType(type);
            otp.setOtpCode(StringUtil.generateOtp());
            otp.setSendTo(phoneDetail.getDialCode() + StringUtil.normalizePhoneNumber(phoneDetail.getNumber()));
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            // send whatsapp
            WablasSendMessageRequest request = new WablasSendMessageRequest();

            request.setPhone(StringUtil.normalizePhoneNumber(otp.getSendTo()));
            request.setMessage(wablasSendOtpCard.replace("#", otp.getOtpCode()));

            new Thread(() -> {
                try {
                    httpRequestUtil.sendPost(request);
                } catch (Exception e) {
                    LOGGER.error("Error processing data", e);
                }
            }).start();
            // end of send whatsapp

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyCardEmail(Long cardId) {
        try {
            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            if (card.getIsEmailVerified() == 1) {
                LOGGER.error("Your Card Email is Already Verified");
                throw new ConflictException("Your Card Email is Already Verified");
            }

            OtpType type = otpTypeRepo.getDetail(2);
            if (type == null) {
                LOGGER.error("OTP Type ID: " + 2 + " is not found");
                throw new NotFoundException("OTP Type ID: " + 2);
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 3);

            UserOtp otp;
            Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    2, // email
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                otp = page.getContent().get(0);
                otp.setUpdatedDate(new Date());
            } else {
                otp = new UserOtp();
            }

            otp.setUserCard(card);
            otp.setOtpType(type);
            otp.setOtpCode(StringUtil.generateOtp());
            otp.setSendTo(card.getEmail());
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            // send email
            emailUtil.sendEmail(
                    otp.getSendTo(),
                    subjectEmailOtpCard,
                    "Your OTP Code is: " + otp.getOtpCode());
            // end of send email

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity challengeCardWhatsapp(Long cardId, String code) {
        try {
            Date today = new Date();
            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    1, // whatsapp
                    code,
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                UserOtp otp = page.getContent().get(0);
                if (today.before(otp.getExpiredDate())) {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    // update verification point
//                    card.setVerificationPoint(card.getVerificationPoint() + 20);
                    card.setIsWhatsappNoVerified(1);
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);
                    // end of update verification point

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
                LOGGER.error("Your OTP Code: " + code + " is invalid");
                throw new NotFoundException("OTP Code: " + code + " is invalid", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity challengeCardEmail(Long cardId, String code) {
        try {
            Date today = new Date();
            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    2, // email
                    code,
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                UserOtp otp = page.getContent().get(0);
                if (today.before(otp.getExpiredDate())) {
                    otp.setIsActive(0);
                    otp.setUpdatedDate(new Date());

                    userOtpRepo.save(otp);

                    // update verification point
//                    card.setVerificationPoint(card.getVerificationPoint() + 20);
                    card.setIsEmailVerified(1);
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);
                    // end of update verification point

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
                LOGGER.error("OTP Code: " + code + " is invalid");
                throw new NotFoundException("OTP Code: " + code + " is invalid", 404);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity peekCardOtp(Long cardId, int type) {
        try {
            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            Page<UserOtp> pageOtp = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    type,
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (pageOtp.getContent() != null && pageOtp.getContent().size() > 0) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        pageOtp.getContent().get(0).getOtpCode()), HttpStatus.OK);
            } else {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
