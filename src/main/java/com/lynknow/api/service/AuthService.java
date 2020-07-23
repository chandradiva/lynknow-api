package com.lynknow.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface AuthService {

    OAuth2AccessToken getToken(HashMap<String, String> params) throws HttpRequestMethodNotSupportedException;
    ResponseEntity logout(HttpServletRequest request);
    ResponseEntity getUserSession();

}
