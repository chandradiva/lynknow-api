package com.lynknow.api.controller;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.LoginRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDataRepository userDataRepo;

    @PostMapping("login")
    public ResponseEntity<OAuth2AccessToken> login(@RequestBody LoginRequest request) {
        HashMap<String, String> params = request.getMap();

        UserData checkUser = userDataRepo.getByUsername(params.get("username"));
        if (checkUser == null) {
            LOGGER.error("Username: " + params.get("username") + " is not found");
            throw new NotFoundException("Username: " + params.get("username"));
        }

        try {
            OAuth2AccessToken token = this.authService.getToken(params);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    token), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if (checkUser != null) {
                throw new BadRequestException("Your password is incorrect");
            }
        }

        throw new InternalServerErrorException();
    }

    @PostMapping("logout")
    public ResponseEntity logout(HttpServletRequest request) {
        return authService.logout(request);
    }

    @GetMapping("get")
    public ResponseEntity getUserSession() {
        return authService.getUserSession();
    }

}
