package com.lynknow.api.service.impl;

import com.lynknow.api.model.CardVerification;
import com.lynknow.api.model.CardVerificationItem;
import com.lynknow.api.model.UserCard;
import com.lynknow.api.repository.CardVerificationItemRepository;
import com.lynknow.api.repository.CardVerificationRepository;
import com.lynknow.api.service.CardVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CardVerificationServiceImpl implements CardVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardVerificationServiceImpl.class);

    @Autowired
    private CardVerificationItemRepository cardVerificationItemRepo;

    @Autowired
    private CardVerificationRepository cardVerificationRepo;

    @Override
    public void initCardVerification(UserCard card) {
        try {
            if (card.getCardType().getId() == 1) {
                // personal card
                CardVerificationItem itemName = cardVerificationItemRepo.getDetail(1);
                CardVerificationItem itemAddress = cardVerificationItemRepo.getDetail(2);

                for (int i = 1; i <= 2; i++) {
                    CardVerification verification = new CardVerification();

                    verification.setUserCard(card);
                    verification.setCreatedDate(new Date());
                    verification.setIsVerified(0);

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

}
