package com.lynknow.api.service.impl;

import com.lynknow.api.exception.ConflictException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.RoleData;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.UserDataRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.RoleDataRepository;
import com.lynknow.api.repository.SubscriptionPackageRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.AuthService;
import com.lynknow.api.service.UserDataService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Date;
import java.util.HashMap;

@Service
public class UserDataServiceImpl implements UserDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataServiceImpl.class);

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private RoleDataRepository roleDataRepo;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity registerAdmin(UserDataRequest request) {
        try {
            RoleData role = roleDataRepo.getDetail(1);
            if (role == null) {
                LOGGER.error("Role ID: " + 1 + " is not found");
                throw new NotFoundException("Role ID: " + 1);
            }

            if (!this.checkByUsername(request.getEmail(), null)) {
                LOGGER.error("Email: " + request.getEmail() + " already exist");
                throw new ConflictException("Email: " + request.getEmail() + " already exist");
            }

            UserData user = new UserData();

            user.setRoleData(role);
            user.setCurrentSubscriptionPackage(null);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(encoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setJoinDate(new Date());
            user.setCreatedDate(new Date());

            userDataRepo.save(user);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    201,
                    "Success",
                    GenerateResponseUtil.generateResponseUser(user)), HttpStatus.CREATED);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity registerNewUser(UserDataRequest request) {
        try {
            RoleData role = roleDataRepo.getDetail(2);
            if (role == null) {
                LOGGER.error("Role ID: " + 2 + " is not found");
                throw new NotFoundException("Role ID: " + 2);
            }

            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(1);
            if (subs == null) {
                LOGGER.error("Subscription Package ID: " + 1 + " is not found");
                throw new NotFoundException("Subscription Package ID: " + 1);
            }

            if (!this.checkByUsername(request.getEmail(), null)) {
                LOGGER.error("Email: " + request.getEmail() + " already exist");
                throw new ConflictException("Email: " + request.getEmail() + " already exist");
            }

            UserData user = new UserData();

            user.setRoleData(role);
            user.setCurrentSubscriptionPackage(subs);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(encoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setJoinDate(new Date());
            user.setCreatedDate(new Date());

            userDataRepo.save(user);

            // auto login after register
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", request.getUsername());
            maps.put("password", request.getPassword());

            HashMap<String, String> params = maps;
            OAuth2AccessToken token = this.authService.getToken(params);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    201,
                    "Success",
                    token), HttpStatus.CREATED);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (HttpRequestMethodNotSupportedException e) {
            LOGGER.error("Failed to Get Auth Token", e);
            throw new InternalServerErrorException("Failed to Get Auth Token" + e.getMessage());
        }
    }

    @Override
    public UserData getByUsername(String username) {
        try {
            return userDataRepo.getByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkByUsername(String email, Long id) {
        try {
            UserData chkByUsername = userDataRepo.getByUsername(email);
            if (chkByUsername == null) {
                return true;
            } else {
                if (chkByUsername.getId().equals(id)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
