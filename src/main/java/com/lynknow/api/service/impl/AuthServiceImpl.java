package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.security.ResourceServerConfig;
import com.lynknow.api.service.AuthService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private ClientDetailsService clientDetailsStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Autowired
    public AuthorizationServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setAccessTokenValiditySeconds(-1);

        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public OAuth2AccessToken getToken(HashMap<String, String> params) throws HttpRequestMethodNotSupportedException {
        if (params.get("username") == null) {
            throw new UsernameNotFoundException("username not found");
        }

        if (params.get("password") == null) {
            throw new UsernameNotFoundException("password not found");
        }

        params.put("client_id", ResourceServerConfig.CLIENT_ID);
        params.put("client_secret", ResourceServerConfig.CLIENT_SECRET);
        params.put("grant_type", "password");

        DefaultOAuth2RequestFactory defaultOAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsStore);
        AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(params);
        authorizationRequest.setApproved(true);

        OAuth2Request oauth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(params.get("username"), params.get("password"));
        Authentication authentication = authenticationManager.authenticate(loginToken);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(oauth2Request, authentication);
        authenticationRequest.setAuthenticated(true);

        OAuth2AccessToken token = tokenServices().createAccessToken(authenticationRequest);

        Map<String, Object> adInfo = new HashMap<>();
        adInfo.put("role", null);

        try {
            UserData user = (UserData) authentication.getPrincipal();
            adInfo.put("role", user.getRoleData().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((DefaultOAuth2AccessToken) token).setAdditionalInformation(adInfo);

        return token;
    }

    @Override
    public ResponseEntity logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                String tokenValue = authHeader.replace("Bearer", "").trim();
                OAuth2AccessToken accessToken = tokenStore().readAccessToken(tokenValue);
                tokenStore().removeAccessToken(accessToken);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Your Session is Expired / Token Invalid");
                throw new BadRequestException("Your Session is Expired / Token Invalid");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getUserSession() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseUserComplete(userLogin)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
