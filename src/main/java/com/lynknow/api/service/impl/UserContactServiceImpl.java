package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.UserContact;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserContactResponse;
import com.lynknow.api.repository.UserContactRepository;
import com.lynknow.api.service.UserContactService;
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

import java.util.Date;

@Service
public class UserContactServiceImpl implements UserContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserContactServiceImpl.class);

    @Autowired
    private UserContactRepository userContactRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getListPaginationContact(PaginationModel model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            Page<UserContactResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = userContactRepo.getListPaginationContact(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.DESC, "status")
                                        .and(Sort.by(Sort.Direction.ASC, model.getSortBy())))
                ).map(generateRes::generateResponseUserContact);
            } else {
                page = userContactRepo.getListPaginationContact(
                        userSession.getId(),
                        PageRequest.of(
                                model.getPage(),
                                model.getSize(),
                                Sort.by(Sort.Direction.DESC, "status")
                                        .and(Sort.by(Sort.Direction.ASC, model.getSortBy())))
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
    public ResponseEntity updateStatus(Long id, Integer status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserContact contact = userContactRepo.getDetail(id);
            if (contact != null) {
                if (status != 3) {
                    if (!contact.getExchangeCard().getUserData().getId().equals(userSession.getId())) {
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

                contact.setStatus(status);
                contact.setUpdatedDate(new Date());

                userContactRepo.save(contact);

                if (status == 1) {
                    // accept
                    UserContact exchangeContact = new UserContact();

                    exchangeContact.setUserData(contact.getExchangeCard().getUserData());
                    exchangeContact.setFromCard(contact.getExchangeCard());
                    exchangeContact.setExchangeCard(contact.getFromCard());
                    exchangeContact.setStatus(1);
                    exchangeContact.setFlag(0);
                    exchangeContact.setCreatedDate(new Date());

                    userContactRepo.save(exchangeContact);
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

}
