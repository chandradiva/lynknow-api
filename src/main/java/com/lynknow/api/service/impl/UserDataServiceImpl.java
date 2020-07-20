package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.RoleData;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.UserDataRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.RoleDataRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.UserDataService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserDataServiceImpl implements UserDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataServiceImpl.class);

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private RoleDataRepository roleDataRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public ResponseEntity registerAdmin(UserDataRequest request) {
        try {
            RoleData role = roleDataRepo.getDetail(1);
            if (role == null) {
                LOGGER.error("Role ID: " + 1 + " is not found");
                throw new NotFoundException("Role ID: " + 1);
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
    public UserData getByUsername(String username) {
        try {
            return userDataRepo.getByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
