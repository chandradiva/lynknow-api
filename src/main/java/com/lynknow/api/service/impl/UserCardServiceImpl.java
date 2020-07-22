package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.CardType;
import com.lynknow.api.model.UserCard;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.UserCardRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserCardResponse;
import com.lynknow.api.repository.CardTypeRepository;
import com.lynknow.api.repository.UserCardRepository;
import com.lynknow.api.service.UserCardService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserCardServiceImpl implements UserCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardServiceImpl.class);

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private CardTypeRepository cardTypeRepo;

    @Override
    public ResponseEntity saveData(UserCardRequest request) {
        try {
            CardType type = cardTypeRepo.getDetail(request.getCardTypeId());
            if (type == null) {
                LOGGER.error("Card Type ID: " + request.getCardTypeId() + " is not found");
                throw new NotFoundException("Card Type ID: " + request.getCardTypeId());
            }

            if (request.getId() == null) {
                // new data
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                UserData userSession = (UserData) auth.getPrincipal();

                List<UserCard> cards = userCardRepo.getList(userSession.getId(), null, null, Sort.by("id").ascending());
                int countCard = cards == null ? 0 : cards.size();
                if (userSession.getCurrentSubscriptionPackage().getId() == 1) {
                    // basic
                    if (countCard == 2) {
                        LOGGER.error("You have reached your card limit. User Basic can only have 2 cards");
                        throw new BadRequestException("You have reached your card limit. User Basic can only have 2 cards");
                    }
                } else {
                    // premium
                    if (countCard == 2) {
                        LOGGER.error("You have reached your card limit. User Premium can only have 10 cards");
                        throw new BadRequestException("You have reached your card limit. User Premium can only have 10 cards");
                    }
                }

                UserCard card = new UserCard();

                card.setUserData(userSession);
                card.setCardType(type);
                card.setFrontSide(request.getFrontSide());
                card.setBackSide(request.getBackSide());
                card.setProfilePhoto(request.getProfilePhoto());
                card.setFirstName(request.getFirstName());
                card.setLastName(request.getLastName());
                card.setDesignation(request.getDesignation());
                card.setCompany(request.getCompany());
                card.setAddress1(request.getAddress1());
                card.setAddress2(request.getAddress2());
                card.setCity(request.getCity());
                card.setPostalCode(request.getPostalCode());
                card.setCountry(request.getCountry());
                card.setEmail(request.getEmail());
                card.setWebsite(request.getWebsite());
                card.setWhatsappNo(request.getWhatsappNo());
                card.setMobileNo(request.getMobileNo());
                card.setIsPublished(0);
                card.setUniqueCode(UUID.randomUUID().toString());
                card.setCreatedDate(new Date());
                card.setIsActive(1);

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        201,
                        "Created",
                        GenerateResponseUtil.generateResponseUserCard(card)), HttpStatus.CREATED);
            } else {
                // update data
                UserCard card = userCardRepo.getDetail(request.getId());
                if (card != null) {
                    card.setCardType(type);
                    card.setFrontSide(request.getFrontSide());
                    card.setBackSide(request.getBackSide());
                    card.setProfilePhoto(request.getProfilePhoto());
                    card.setFirstName(request.getFirstName());
                    card.setLastName(request.getLastName());
                    card.setDesignation(request.getDesignation());
                    card.setCompany(request.getCompany());
                    card.setAddress1(request.getAddress1());
                    card.setAddress2(request.getAddress2());
                    card.setCity(request.getCity());
                    card.setPostalCode(request.getPostalCode());
                    card.setCountry(request.getCountry());
                    card.setEmail(request.getEmail());
                    card.setWebsite(request.getWebsite());
                    card.setWhatsappNo(request.getWhatsappNo());
                    card.setMobileNo(request.getMobileNo());
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            GenerateResponseUtil.generateResponseUserCard(card)), HttpStatus.OK);
                } else {
                    LOGGER.error("User Card ID: " + request.getId() + " is not found");
                    throw new NotFoundException("User Card ID: " + request.getId());
                }
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList(Long userId, Integer typeId, Integer isPublished) {
        try {
            List<UserCardResponse> datas = new ArrayList<>();
            List<UserCard> cards = userCardRepo.getList(
                    userId.equals(0) ? null : userId,
                    typeId == 0 ? null : typeId,
                    isPublished == -1 ? null : isPublished,
                    Sort.by("isPublished").descending());

            if (cards != null) {
                for (UserCard item : cards) {
                    datas.add(GenerateResponseUtil.generateResponseUserCard(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    datas), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPagination(Long userId, Integer typeId, Integer isPublished, PaginationModel model) {
        try {
            Page<UserCardResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = userCardRepo.getListPagination(
                        userId.equals(0) ? null : userId,
                        typeId == 0 ? null : typeId,
                        isPublished == -1 ? null : isPublished,
                        model.getParam().toLowerCase(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy()))
                ).map(GenerateResponseUtil::generateResponseUserCard);
            } else {
                page = userCardRepo.getListPagination(
                        userId.equals(0) ? null : userId,
                        typeId == 0 ? null : typeId,
                        isPublished == -1 ? null : isPublished,
                        model.getParam().toLowerCase(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy()))
                ).map(GenerateResponseUtil::generateResponseUserCard);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        GenerateResponseUtil.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity deleteData(Long id) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                card.setIsActive(0);
                card.setDeletedDate(new Date());

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity publishCard(Long id, int type) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                if (type == 1) {
                    // publish
                    card.setIsPublished(1);
                    card.setPublishedDate(new Date());
                } else {
                    // unpublish
                    card.setIsPublished(0);
                    card.setPublishedDate(null);
                }

                card.setUpdatedDate(new Date());

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        GenerateResponseUtil.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
