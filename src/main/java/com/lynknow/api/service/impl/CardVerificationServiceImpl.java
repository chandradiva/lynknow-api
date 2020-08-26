package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.request.VerifyCardRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.CardVerificationResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.CardVerificationService;
import com.lynknow.api.util.GenerateResponseUtil;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class CardVerificationServiceImpl implements CardVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardVerificationServiceImpl.class);

    @Autowired
    private CardVerificationItemRepository cardVerificationItemRepo;

    @Autowired
    private CardVerificationRepository cardVerificationRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private UserOtpRepository userOtpRepo;

    @Autowired
    private OtpTypeRepository otpTypeRepo;

    @Autowired
    private CardVerificationCreditUsageRepository usageRepo;

    @Value("${upload.dir.card.verification}")
    private String cardVerificationDir;

    @Override
    public void initCardVerification(UserCard card) {
        try {
            int count = 0;
            count = cardVerificationRepo.getList(card.getId()).size();
            if (count > 0) {
                return;
            }

            if (card.getCardType().getId() == 1) {
                // personal card
                CardVerificationItem itemName = cardVerificationItemRepo.getDetail(1);
                CardVerificationItem itemAddress = cardVerificationItemRepo.getDetail(2);

                for (int i = 1; i <= 2; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            } else if (card.getCardType().getId() == 2) {
                // company card
                CardVerificationItem itemComName = cardVerificationItemRepo.getDetail(4);
                CardVerificationItem itemComContact = cardVerificationItemRepo.getDetail(5);
                CardVerificationItem itemComAddress = cardVerificationItemRepo.getDetail(6);

                for (int i = 1; i <= 3; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemComName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemComContact);
                    } else if (i == 3) {
                        verification.setCardVerificationItem(itemComAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            } else if (card.getCardType().getId() == 3) {
                // employee card
                CardVerificationItem itemName = cardVerificationItemRepo.getDetail(1);
                CardVerificationItem itemDesignation = cardVerificationItemRepo.getDetail(3);
                CardVerificationItem itemComName = cardVerificationItemRepo.getDetail(4);
                CardVerificationItem itemComContact = cardVerificationItemRepo.getDetail(5);
                CardVerificationItem itemComAddress = cardVerificationItemRepo.getDetail(6);

                for (int i = 1; i <= 5; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemDesignation);
                    } else if (i == 3) {
                        verification.setCardVerificationItem(itemComName);
                    } else if (i == 4) {
                        verification.setCardVerificationItem(itemComContact);
                    } else if (i == 5) {
                        verification.setCardVerificationItem(itemComAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
        }
    }

    @Override
    public ResponseEntity initCardVerification(Long cardId) {
        try {
            int count = 0;
            count = cardVerificationRepo.getList(cardId).size();
            if (count > 0) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            }

            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            if (card.getCardType().getId() == 1) {
                // personal card
                CardVerificationItem itemName = cardVerificationItemRepo.getDetail(1);
                CardVerificationItem itemAddress = cardVerificationItemRepo.getDetail(2);

                for (int i = 1; i <= 2; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            } else if (card.getCardType().getId() == 2) {
                // company card
                CardVerificationItem itemComName = cardVerificationItemRepo.getDetail(4);
                CardVerificationItem itemComContact = cardVerificationItemRepo.getDetail(5);
                CardVerificationItem itemComAddress = cardVerificationItemRepo.getDetail(6);

                for (int i = 1; i <= 3; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemComName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemComContact);
                    } else if (i == 3) {
                        verification.setCardVerificationItem(itemComAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            } else if (card.getCardType().getId() == 3) {
                // employee card
                CardVerificationItem itemName = cardVerificationItemRepo.getDetail(1);
                CardVerificationItem itemDesignation = cardVerificationItemRepo.getDetail(3);
                CardVerificationItem itemComName = cardVerificationItemRepo.getDetail(4);
                CardVerificationItem itemComContact = cardVerificationItemRepo.getDetail(5);
                CardVerificationItem itemComAddress = cardVerificationItemRepo.getDetail(6);

                for (int i = 1; i <= 5; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);
                    verification.setIsRequested(0);

                    if (i == 1) {
                        verification.setCardVerificationItem(itemName);
                    } else if (i == 2) {
                        verification.setCardVerificationItem(itemDesignation);
                    } else if (i == 3) {
                        verification.setCardVerificationItem(itemComName);
                    } else if (i == 4) {
                        verification.setCardVerificationItem(itemComContact);
                    } else if (i == 5) {
                        verification.setCardVerificationItem(itemComAddress);
                    }

                    cardVerificationRepo.save(verification);
                }
            }

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
    public ResponseEntity requestToVerify(MultipartFile file, Long cardId, Integer itemId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                LOGGER.error("Only Premium Users that can Verify their Card with Verification Credit");
                throw new BadRequestException("Only Premium Users that can Verify their Card with Verification Credit");
            }

            if (userLogin.getMaxVerificationCredit() == userLogin.getCurrentVerificationCredit()) {
                LOGGER.error("You're Running Out of Verification Credit");
                throw new BadRequestException("You're Running Out of Verification Credit");
            }

            CardVerification verification = cardVerificationRepo.getDetail(cardId, itemId);
            if (verification != null) {
                if (!verification.getUserCard().getUserData().getId().equals(userLogin.getId())) {
                    LOGGER.error("You Can't Request to Verify Other User Card");
                    throw new UnprocessableEntityException("You Can't Request to Verify Other User Card");
                }

                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                fileName = fileName.replaceAll("\\s+", "_");

                int idx = fileName.lastIndexOf(".");
                String ext = fileName.substring(idx);

                String newFilename = UUID.randomUUID() + ext;
                File uploadDir = new File(cardVerificationDir);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // update status
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 3);

                verification.setParam(newFilename);
                verification.setIsRequested(1);
                verification.setUpdatedDate(new Date());
                verification.setExpiredDate(cal.getTime());

                cardVerificationRepo.save(verification);

                // save credit usage
                CardVerificationCreditUsage usage = new CardVerificationCreditUsage();

                usage.setCardVerification(verification);
                usage.setCreatedDate(new Date());

                usageRepo.save(usage);
                // end of save credit usage

                // update current usage
                userLogin.setCurrentVerificationCredit(userLogin.getCurrentVerificationCredit() + 1);
                userLogin.setUpdatedDate(new Date());

                userDataRepo.save(userLogin);
                // end of update current usage

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Verification with Card ID: " + cardId + " and Item ID: " + itemId + " is not found");
                throw new NotFoundException("Card Verification with Card ID: " + cardId + " and Item ID: " + itemId);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity requestToVerify(String officePhone, Long cardId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                LOGGER.error("Only Premium Users that can Verify their Card with Verification Credit");
                throw new BadRequestException("Only Premium Users that can Verify their Card with Verification Credit");
            }

            if (userLogin.getMaxVerificationCredit() == userLogin.getCurrentVerificationCredit()) {
                LOGGER.error("You're Running Out of Verification Credit");
                throw new BadRequestException("You're Running Out of Verification Credit");
            }

            // 5 = company contact
            CardVerification verification = cardVerificationRepo.getDetail(cardId, 5);
            if (verification != null) {
                if (!verification.getUserCard().getUserData().getId().equals(userLogin.getId())) {
                    LOGGER.error("You Can't Request to Verify Other User Card");
                    throw new UnprocessableEntityException("You Can't Request to Verify Other User Card");
                }

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 3);

                verification.setParam(officePhone);
                verification.setIsRequested(1);
                verification.setUpdatedDate(new Date());
                verification.setExpiredDate(cal.getTime());

                cardVerificationRepo.save(verification);

                // save credit usage
                CardVerificationCreditUsage usage = new CardVerificationCreditUsage();

                usage.setCardVerification(verification);
                usage.setCreatedDate(new Date());

                usageRepo.save(usage);
                // end of save credit usage

                // update current usage
                userLogin.setCurrentVerificationCredit(userLogin.getCurrentVerificationCredit() + 1);
                userLogin.setUpdatedDate(new Date());

                userDataRepo.save(userLogin);
                // end of update current usage

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Verification with Card ID: " + cardId + " and Item ID: 5 is not found");
                throw new NotFoundException("Card Verification with Card ID: " + cardId + " and Item ID: 5");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            CardVerification verification = cardVerificationRepo.getDetail(id);
            if (verification != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Verification ID: " + id + " is not found");
                throw new NotFoundException("Card Verification ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long cardId, Integer itemId) {
        try {
            CardVerification verification = cardVerificationRepo.getDetail(cardId, itemId);
            if (verification != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Verification with Card ID: " + cardId + " and Item ID: " + itemId + " is not found");
                throw new NotFoundException("Card Verification with Card ID: " + cardId + " and Item ID: " + itemId);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList(Long cardId) {
        try {
            List<CardVerificationResponse> datas = new ArrayList<>();
            List<CardVerification> verifications = cardVerificationRepo.getList(cardId);
            if (verifications != null) {
                for (CardVerification item : verifications) {
                    datas.add(generateRes.generateResponseCardVerification(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    datas), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyRequest(VerifyCardRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            if (userSession.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can Verify Request for Card Verification");
                throw new BadRequestException("Only Administrator Roles That Can Verify Request for Card Verification");
            }

            if (request.getVerify() > 1 || request.getVerify() < 0) {
                LOGGER.error("Verify Data Can Only be Filled with 0 or 1");
                throw new UnprocessableEntityException("Verify Data Can Only be Filled with 0 or 1");
            }

            CardVerification verification = cardVerificationRepo.getDetail(request.getCardId(), request.getItemId());
            if (verification != null) {
                if (verification.getIsRequested() == 0) {
                    LOGGER.error("Card Owner Not Yet Requested for Card Verification");
                    throw new UnprocessableEntityException("Card Owner Not Yet Requested for Card Verification");
                }

                if (request.getVerify() == 1) {
                    verification.setIsVerified(1);
                    verification.setVerifiedBy(userSession);
                    verification.setVerifiedDate(new Date());

                    this.adjustCardVerificationPoint(verification.getUserCard());
                } else {
                    verification.setIsVerified(0);
                    verification.setReason(request.getReason());
                }

                verification.setUpdatedDate(new Date());

                cardVerificationRepo.save(verification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Verification with Card ID: " + request.getCardId() + " and Item ID: " + request.getItemId() + " is not found");
                throw new NotFoundException("Card Verification with Card ID: " + request.getCardId() + " and Item ID: " + request.getItemId());
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity generateOtpCompanyPhoneNumber(Long cardId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            if (userSession.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator that Can Generate OTP for Company Phone Number");
                throw new UnprocessableEntityException("Only Administrator that Can Generate OTP for Company Phone Number");
            }

            CardVerification verification = cardVerificationRepo.getDetail(cardId, 5);
            if (verification == null) {
                LOGGER.error("Card Verification with Card ID: " + cardId + " and Item ID: 5 is not found");
                throw new NotFoundException("Card Verification with Card ID: " + cardId + " and Item ID: 5");
            }

            if (verification.getIsRequested() == 0) {
                LOGGER.error("Card Owner Not Yet Requested for Card Verification");
                throw new UnprocessableEntityException("Card Owner Not Yet Requested for Card Verification");
            }

            UserCard card = userCardRepo.getDetail(cardId);
            if (card == null) {
                LOGGER.error("User Card ID: " + cardId + " is not found");
                throw new NotFoundException("User Card ID: " + cardId);
            }

            OtpType type = otpTypeRepo.getDetail(3);
            if (type == null) {
                LOGGER.error("OTP Type ID: " + 2 + " is not found");
                throw new NotFoundException("OTP Type ID: " + 3);
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 72);

            UserOtp otp;
            Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                    card.getId(),
                    3, // company phone number
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
            otp.setSendTo(verification.getParam());
            otp.setExpiredDate(cal.getTime());
            otp.setCreatedDate(new Date());
            otp.setIsActive(1);

            userOtpRepo.save(otp);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    otp.getOtpCode()), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity challengeCompanyPhoneNumber(Long cardId, String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            Date today = new Date();
            CardVerification verification = cardVerificationRepo.getDetail(cardId, 5);
            if (verification != null) {
                if (!verification.getUserCard().getUserData().getId().equals(userSession.getId())) {
                    LOGGER.error("You Can't Challenge OTP Other User Card");
                    throw new UnprocessableEntityException("You Can't Challenge OTP Other User Card");
                }

                if (verification.getIsRequested() == 0) {
                    LOGGER.error("You Haven't Requested for Card Verification");
                    throw new UnprocessableEntityException("You Haven't Requested for Card Verification");
                }

                Page<UserOtp> page = userOtpRepo.getDetailCardOtp(
                        cardId,
                        3, // company phone number
                        code,
                        PageRequest.of(0, 1, Sort.by("id").descending()));
                if (page.getContent() != null && page.getContent().size() > 0) {
                    UserOtp otp = page.getContent().get(0);
                    if (today.before(otp.getExpiredDate())) {
                        otp.setIsActive(0);
                        otp.setUpdatedDate(new Date());

                        userOtpRepo.save(otp);

                        verification.setIsVerified(1);
                        verification.setVerifiedDate(new Date());
                        verification.setUpdatedDate(new Date());

                        this.adjustCardVerificationPoint(verification.getUserCard());

                        cardVerificationRepo.save(verification);

                        return new ResponseEntity(new BaseResponse<>(
                                true,
                                200,
                                "Success",
                                generateRes.generateResponseCardVerification(verification)), HttpStatus.OK);
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
            } else {
                LOGGER.error("Card Verification with Card ID: " + cardId + " and Item ID: 5 is not found");
                throw new NotFoundException("Card Verification with Card ID: " + cardId + " and Item ID: 5");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity checkCredit() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                LOGGER.error("Only Premium Users that can Verify their Card with Verification Credit");
                throw new BadRequestException("Only Premium Users that can Verify their Card with Verification Credit");
            }

            if (userLogin.getMaxVerificationCredit() == userLogin.getCurrentVerificationCredit()) {
                LOGGER.error("You're Running Out of Verification Credit");
                throw new BadRequestException("You're Running Out of Verification Credit");
            }

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

    private void adjustCardVerificationPoint(UserCard card) {
        try {
            int point = 0;
            int i = 0;
            List<CardVerification> verifications = cardVerificationRepo.getList(card.getId());
            if (verifications != null) {
                for (CardVerification item : verifications) {
                    if (item.getIsVerified() == 1) {
                        i++;
                    }
                }
            }

            if (card.getCardType().getId() == 1) {
                // personal card
                point = 100 / 2;

                if (i == 2) {
                    card.setVerificationPoint(100);
                } else {
                    card.setVerificationPoint(card.getVerificationPoint() + point);
                }
            } else if (card.getCardType().getId() == 2) {
                // company card
                point = 100 / 3;

                if (i == 3) {
                    card.setVerificationPoint(100);
                } else {
                    card.setVerificationPoint(card.getVerificationPoint() + point);
                }
            } else if (card.getCardType().getId() == 3) {
                // employee card
                point = 100 / 5;

                if (i == 5) {
                    card.setVerificationPoint(100);
                } else {
                    card.setVerificationPoint(card.getVerificationPoint() + point);
                }
            }

            card.setUpdatedDate(new Date());

            userCardRepo.save(card);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

}
