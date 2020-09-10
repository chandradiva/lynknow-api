package com.lynknow.api.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lynknow.api.exception.ConflictException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.RoleData;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.UserProfile;
import com.lynknow.api.pojo.request.AuthSocialRequest;
import com.lynknow.api.pojo.request.UserDataRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.RoleDataRepository;
import com.lynknow.api.repository.SubscriptionPackageRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.repository.UserProfileRepository;
import com.lynknow.api.service.AuthService;
import com.lynknow.api.service.UserDataService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${google.client.id}")
    private String googleClientId;

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
            user.setUsername(request.getEmail().toLowerCase());
            user.setEmail(request.getEmail().toLowerCase());
            user.setPassword(encoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setJoinDate(new Date());
            user.setCreatedDate(new Date());

            userDataRepo.save(user);

            UserProfile profile = new UserProfile();

            profile.setUserData(user);
            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setIsWhatsappNoVerified(0);
            profile.setIsEmailVerified(0);
            profile.setCreatedDate(new Date());
            profile.setIsActive(1);

            userProfileRepo.save(profile);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    201,
                    "Success",
                    generateRes.generateResponseUser(user)), HttpStatus.CREATED);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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

            if (!this.checkByUsername(request.getUsername(), null)) {
                LOGGER.error("Username: " + request.getEmail() + " already exist");
                throw new ConflictException("Username: " + request.getEmail() + " already exist");
            }

            if (!this.checkByEmail(request.getEmail(), null)) {
                LOGGER.error("Email: " + request.getEmail() + " already exist");
                throw new ConflictException("Email: " + request.getEmail() + " already exist");
            }

            UserData user = new UserData();

            user.setRoleData(role);
            user.setCurrentSubscriptionPackage(subs);
            user.setUsername(request.getEmail().toLowerCase());
            user.setEmail(request.getEmail().toLowerCase());
            user.setPassword(encoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setJoinDate(new Date());
            user.setCreatedDate(new Date());

            userDataRepo.save(user);

            UserProfile profile = new UserProfile();

            profile.setUserData(user);
            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setIsWhatsappNoVerified(0);
            profile.setIsEmailVerified(0);
            profile.setCreatedDate(new Date());
            profile.setIsActive(1);

            userProfileRepo.save(profile);

            // auto login after register
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", request.getUsername().toLowerCase());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (HttpRequestMethodNotSupportedException e) {
            LOGGER.error("Failed to Get Auth Token", e);
            throw new InternalServerErrorException("Failed to Get Auth Token: " + e.getMessage());
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

    @Override
    public ResponseEntity registerFacebook(AuthSocialRequest request) {
        try {
            Facebook fb = new FacebookTemplate(request.getToken(), "", facebookAppId);
            String[] fields = {"id", "email", "first_name", "last_name", "gender", "birthday"};
            User userFb = fb.fetchObject("me", User.class, fields);

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

            UserData user;
            Page<UserData> pageUser = userDataRepo.getByFbId(
                    userFb.getId(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                user = pageUser.getContent().get(0);

                // auto login if facebook already registered
                HashMap<String, String> maps = new HashMap<>();
                maps.put("username", user.getEmail().toLowerCase());
                maps.put("password", userFb.getId());

                HashMap<String, String> params = maps;
                OAuth2AccessToken token = this.authService.getToken(params);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        201,
                        "Success",
                        token), HttpStatus.CREATED);
            } else {
                user = new UserData();

                user.setRoleData(role);
                user.setCurrentSubscriptionPackage(subs);
                user.setPassword(encoder.encode(userFb.getId()));
                user.setJoinDate(new Date());
                user.setCreatedDate(new Date());
                user.setFbId(userFb.getId());
                user.setFbEmail(userFb.getEmail());

                if (userFb.getEmail() == null) {
                    user.setUsername(userFb.getId() + "@facebook.com");
                    user.setEmail(userFb.getId() + "@facebook.com");
                } else {
                    user.setUsername(userFb.getEmail().toLowerCase());
                    user.setEmail(userFb.getEmail().toLowerCase());
                }

                if (userFb.getFirstName() == null) {
                    user.setFirstName(userFb.getId() + "@facebook.com");
                    user.setLastName("");
                } else {
                    user.setFirstName(userFb.getFirstName());
                    user.setLastName(userFb.getLastName());
                }

                userDataRepo.save(user);

                UserProfile profile = new UserProfile();

                profile.setUserData(user);
                profile.setIsWhatsappNoVerified(0);
                profile.setIsEmailVerified(0);
                profile.setCreatedDate(new Date());
                profile.setIsActive(1);

                if (userFb.getFirstName() == null) {
                    profile.setFirstName(userFb.getId() + "@facebook.com");
                    profile.setLastName("");
                } else {
                    profile.setFirstName(userFb.getFirstName());
                    profile.setLastName(userFb.getLastName());
                }

                userProfileRepo.save(profile);

                // auto login after register
                HashMap<String, String> maps = new HashMap<>();
                maps.put("username", userFb.getEmail().toLowerCase());
                maps.put("password", userFb.getId());

                HashMap<String, String> params = maps;
                OAuth2AccessToken token = this.authService.getToken(params);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        201,
                        "Success",
                        token), HttpStatus.CREATED);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (HttpRequestMethodNotSupportedException e) {
            LOGGER.error("Failed to Get Auth Token", e);
            throw new InternalServerErrorException("Failed to Get Auth Token: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity registerGoogle(AuthSocialRequest request) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

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

                UserData user;
                Page<UserData> page = userDataRepo.getByGoogleId(
                        payload.getSubject(),
                        PageRequest.of(0, 1, Sort.by("id").descending()));
                if (page.getContent() != null && page.getContent().size() > 0) {
                    user = page.getContent().get(0);

                    // auto login if google already registered
                    HashMap<String, String> maps = new HashMap<>();
                    maps.put("username", user.getEmail().toLowerCase());
                    maps.put("password", payload.getSubject());

                    HashMap<String, String> params = maps;
                    OAuth2AccessToken token = this.authService.getToken(params);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            201,
                            "Success",
                            token), HttpStatus.CREATED);
                } else {
                    user = new UserData();

                    user.setUsername(payload.getEmail());
                    user.setEmail(payload.getEmail());
                    user.setFirstName((String) payload.get("given_name"));
                    user.setLastName((String) payload.get("family_name"));
                    user.setRoleData(role);
                    user.setCurrentSubscriptionPackage(subs);
                    user.setPassword(encoder.encode(payload.getSubject()));
                    user.setJoinDate(new Date());
                    user.setCreatedDate(new Date());
                    user.setGoogleId(payload.getSubject());
                    user.setGoogleEmail(payload.getEmail());

                    userDataRepo.save(user);

                    UserProfile profile = new UserProfile();

                    profile.setUserData(user);
                    profile.setFirstName((String) payload.get("given_name"));
                    profile.setLastName((String) payload.get("family_name"));
                    profile.setIsWhatsappNoVerified(0);
                    profile.setIsEmailVerified(0);
                    profile.setCreatedDate(new Date());
                    profile.setIsActive(1);

                    userProfileRepo.save(profile);

                    // auto login after register
                    HashMap<String, String> maps = new HashMap<>();
                    maps.put("username", payload.getEmail());
                    maps.put("password", payload.getSubject());

                    HashMap<String, String> params = maps;
                    OAuth2AccessToken token = this.authService.getToken(params);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            201,
                            "Success",
                            token), HttpStatus.CREATED);
                }
            } else {
                LOGGER.error("Invalid Token");
                throw new InternalServerErrorException("Invalid Token");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (HttpRequestMethodNotSupportedException e) {
            LOGGER.error("Failed to Get Auth Token", e);
            throw new InternalServerErrorException("Failed to Get Auth Token: " + e.getMessage());
        }
    }

    private boolean checkByUsername(String username, Long id) {
        try {
            UserData chkByUsername = null;
            Page<UserData> pageUser = userDataRepo.getByUsername(
                    username.toLowerCase(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                chkByUsername = pageUser.getContent().get(0);
            }

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
            return false;
        }
    }

    private boolean checkByEmail(String email, Long id) {
        try {
            UserData chkByEmail = null;
            Page<UserData> pageUser = userDataRepo.getByEmail(
                    email.toLowerCase(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                chkByEmail = pageUser.getContent().get(0);
            }

            if (chkByEmail == null) {
                return true;
            } else {
                if (chkByEmail.getId().equals(id)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

}
