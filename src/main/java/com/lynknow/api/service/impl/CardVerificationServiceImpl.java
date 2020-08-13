package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.CardVerification;
import com.lynknow.api.model.CardVerificationItem;
import com.lynknow.api.model.UserCard;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.CardVerificationItemRepository;
import com.lynknow.api.repository.CardVerificationRepository;
import com.lynknow.api.repository.UserCardRepository;
import com.lynknow.api.service.CardVerificationService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity requestToVerify(MultipartFile file, Long cardId, Integer itemId) {
        try {
            CardVerification verification = cardVerificationRepo.getDetail(cardId, itemId);
            if (verification != null) {
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
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity requestToVerify(String officePhone, Long cardId) {
        try {
            // 5 = company contact
            CardVerification verification = cardVerificationRepo.getDetail(cardId, 5);
            if (verification != null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 3);

                verification.setParam(officePhone);
                verification.setIsRequested(1);
                verification.setUpdatedDate(new Date());
                verification.setExpiredDate(cal.getTime());

                cardVerificationRepo.save(verification);

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
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
