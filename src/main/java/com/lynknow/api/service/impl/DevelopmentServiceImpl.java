package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.SubscriptionPackageRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.DevelopmentService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DevelopmentServiceImpl implements DevelopmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentServiceImpl.class);

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity setToPremium() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            // 2 = premium
            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(2);
            if (subs == null) {
                LOGGER.error("Subscription Package ID: " + 2 + " is not found");
                throw new NotFoundException("Subscription Package ID: " + 2);
            }

            userLogin.setCurrentSubscriptionPackage(subs);
            userLogin.setUpdatedDate(new Date());

            userDataRepo.save(userLogin);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseUser(userLogin)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity setToBasic() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            // 1 = basic
            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(1);
            if (subs == null) {
                LOGGER.error("Subscription Package ID: " + 1 + " is not found");
                throw new NotFoundException("Subscription Package ID: " + 1);
            }

            userLogin.setCurrentSubscriptionPackage(subs);
            userLogin.setUpdatedDate(new Date());

            userDataRepo.save(userLogin);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseUser(userLogin)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
