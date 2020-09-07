package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.view.ViewTotalCard;
import com.lynknow.api.model.view.ViewTotalPendingCardVerification;
import com.lynknow.api.model.view.ViewTotalPendingPersonalVerification;
import com.lynknow.api.model.view.ViewTotalUser;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.repository.view.ViewTotalCardRepository;
import com.lynknow.api.repository.view.ViewTotalPendingCardVerificationRepository;
import com.lynknow.api.repository.view.ViewTotalPendingPersonalVerificationRepository;
import com.lynknow.api.repository.view.ViewTotalUserRepository;
import com.lynknow.api.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private ViewTotalUserRepository viewTotalUserRepo;

    @Autowired
    private ViewTotalCardRepository viewTotalCardRepo;

    @Autowired
    private ViewTotalPendingCardVerificationRepository viewTotalPendingCardVerificationRepo;

    @Autowired
    private ViewTotalPendingPersonalVerificationRepository viewTotalPendingPersonalVerificationRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Override
    public ResponseEntity getDataTotalUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can View Dashboard Data");
                throw new BadRequestException("Only Administrator Roles That Can View Dashboard Data");
            }

            Page<ViewTotalUser> page = viewTotalUserRepo.getData(Pageable.unpaged());
            if (page.getContent() != null && page.getContent().size() > 0) {
                ViewTotalUser view = page.getContent().get(0);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        view), HttpStatus.OK);
            } else {
                LOGGER.error("Unable to Get Dashboard Data");
                throw new UnprocessableEntityException("Unable to Get Dashboard Data");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDataTotalCard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can View Dashboard Data");
                throw new BadRequestException("Only Administrator Roles That Can View Dashboard Data");
            }

            Page<ViewTotalCard> page = viewTotalCardRepo.getData(Pageable.unpaged());
            if (page.getContent() != null && page.getContent().size() > 0) {
                ViewTotalCard view = page.getContent().get(0);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        view), HttpStatus.OK);
            } else {
                LOGGER.error("Unable to Get Dashboard Data");
                throw new UnprocessableEntityException("Unable to Get Dashboard Data");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDataTotalCardVerification() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can View Dashboard Data");
                throw new BadRequestException("Only Administrator Roles That Can View Dashboard Data");
            }

            Page<ViewTotalPendingCardVerification> page = viewTotalPendingCardVerificationRepo.getData(Pageable.unpaged());
            if (page.getContent() != null && page.getContent().size() > 0) {
                ViewTotalPendingCardVerification view = page.getContent().get(0);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        view), HttpStatus.OK);
            } else {
                LOGGER.error("Unable to Get Dashboard Data");
                throw new UnprocessableEntityException("Unable to Get Dashboard Data");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDataTotalPersonalVerification() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can View Dashboard Data");
                throw new BadRequestException("Only Administrator Roles That Can View Dashboard Data");
            }

            Page<ViewTotalPendingPersonalVerification> page = viewTotalPendingPersonalVerificationRepo.getData(Pageable.unpaged());
            if (page.getContent() != null && page.getContent().size() > 0) {
                ViewTotalPendingPersonalVerification view = page.getContent().get(0);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        view), HttpStatus.OK);
            } else {
                LOGGER.error("Unable to Get Dashboard Data");
                throw new UnprocessableEntityException("Unable to Get Dashboard Data");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
