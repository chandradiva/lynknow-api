package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserContactResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.UserContactService;
import com.lynknow.api.util.EmailUtil;
import com.lynknow.api.util.GenerateResponseUtil;
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

import java.util.Date;
import java.util.List;

@Service
public class UserContactServiceImpl implements UserContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContactServiceImpl.class);

    @Autowired
    private UserContactRepository userContactRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private NotificationTypeRepository notificationTypeRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserDataRepository userDataRepo;

    @Value("${fe.url.view-user-card}")
    private String viewUserCardUrl;

    @Override
    public ResponseEntity getListPaginationContact(PaginationModel model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            Page<UserContactResponse> page;
            if (model.getSort().equals("asc")) {
                page = userContactRepo.getListPaginationContact(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                Integer.MAX_VALUE,
                                Sort.by(Sort.Direction.ASC, "userData.firstName"))
                ).map(generateRes::generateResponseUserContact);
            } else {
                page = userContactRepo.getListPaginationContact(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                Integer.MAX_VALUE,
                                Sort.by(Sort.Direction.DESC, "userData.firstName"))
                ).map(generateRes::generateResponseUserContact);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPaginationRequested(PaginationModel model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            Page<UserContactResponse> page;
            if (model.getSort().equals("asc")) {
                page = userContactRepo.getListPaginationRequested(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.DESC, "status")
                                        .and(Sort.by(Sort.Direction.ASC, model.getSortBy())))
                ).map(generateRes::generateResponseUserContact);
            } else {
                page = userContactRepo.getListPaginationRequested(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.DESC, "status")
                                        .and(Sort.by(Sort.Direction.DESC, model.getSortBy())))
                ).map(generateRes::generateResponseUserContact);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPaginationReceived(PaginationModel model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            Page<UserContactResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = userContactRepo.getListPaginationReceived(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.ASC, "status")
                                        .and(Sort.by(Sort.Direction.ASC, model.getSortBy())))
                ).map(generateRes::generateResponseUserContact);
            } else {
                page = userContactRepo.getListPaginationReceived(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.ASC, "status")
                                        .and(Sort.by(Sort.Direction.DESC, model.getSortBy())))
                ).map(generateRes::generateResponseUserContact);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity updateStatus(Long id, Integer status, Long cardId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserContact contact = userContactRepo.getDetail(id);
            if (contact != null) {
                if (status != 3) {
                    if (contact.getExchangeCard() != null
                            && !contact.getExchangeCard().getUserData().getId().equals(userSession.getId())
                    ) {
                        LOGGER.error("You Can't Accept or Reject Other User Exchange Card Request");
                        throw new UnprocessableEntityException("You Can't Accept or Reject Other User Exchange Card Request");
                    }
                } else {
                    if (!contact.getUserData().getId().equals(userSession.getId())) {
                        LOGGER.error("You Can't Cancel Other User Exchange Card Request");
                        throw new UnprocessableEntityException("You Can't Cancel Other User Exchange Card Request");
                    }
                }

                if (contact.getStatus() != 0) {
                    LOGGER.error("Current Status Exchange Card is not Requested");
                    throw new UnprocessableEntityException("Current Status Exchange Card is not Requested");
                }

                if (cardId != null) {
                    UserCard exchangeCard = userCardRepo.getDetail(cardId);

                    contact.setExchangeCard(exchangeCard);
                }

                contact.setStatus(status);
                contact.setUpdatedDate(new Date());

                userContactRepo.save(contact);

                if (status == 1) {
                    // accept
                    UserContact exchangeContact = new UserContact();

                    exchangeContact.setExchangeCard(contact.getFromCard());
                    exchangeContact.setExchangeUser(contact.getFromCard().getUserData());
                    exchangeContact.setStatus(1);
                    exchangeContact.setFlag(0);
                    exchangeContact.setCreatedDate(new Date());

                    if (cardId != null) {
                        UserCard exchangeCard = userCardRepo.getDetail(cardId);
                        if (exchangeCard != null) {
                            exchangeContact.setUserData(exchangeCard.getUserData());
                            exchangeContact.setFromCard(exchangeCard);
                        }
                    } else {
                        exchangeContact.setUserData(contact.getExchangeCard().getUserData());
                        exchangeContact.setFromCard(contact.getExchangeCard());
                    }

                    userContactRepo.save(exchangeContact);

                    // save notification data
                    NotificationType type = notificationTypeRepo.getDetail(12); // accept exchange request

                    Notification notification = new Notification();

                    notification.setUserData(userLogin);
                    notification.setTargetUserData(exchangeContact.getExchangeUser());
                    notification.setTargetUserCard(exchangeContact.getExchangeCard());
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);
                    notification.setParamId(exchangeContact.getFromCard().getId());

                    notificationRepo.save(notification);
                    // end of save notification data
                }

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserContact(contact)), HttpStatus.OK);
            } else {
                LOGGER.error("User Contact ID: " + id + " is not found");
                throw new NotFoundException("User Contact ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public void notifyUpdatedCard(UserCard updatedCard) {
        try {
            List<UserContact> contacts = userContactRepo.getList(updatedCard.getUserData().getId(), 1);
            if (contacts != null) {
                for (UserContact item : contacts) {
                    // save notification data
                    NotificationType type = notificationTypeRepo.getDetail(11); // update card info

                    Notification notification = new Notification();

                    notification.setUserData(updatedCard.getUserData());
                    notification.setTargetUserData(item.getExchangeCard().getUserData());
                    notification.setTargetUserCard(item.getExchangeCard());
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);

                    notificationRepo.save(notification);
                    // end of save notification data

                    // send email
                    String url = viewUserCardUrl + updatedCard.getUniqueCode();
                    emailUtil.sendEmailNoThread(
                            item.getExchangeCard().getUserData().getEmail(),
                            "Lynknow - " + updatedCard.getUserData().getFirstName() + " has Updated the Card",
                            "Please click url below to View the Updated Card: <br/><br/> <b><a href=\"" + url + "\">View Card</a></b>");
                    // end of send email
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
