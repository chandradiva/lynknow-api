package com.lynknow.api.service.impl;

import com.lynknow.api.exception.ConflictException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.RoleData;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.UserProfile;
import com.lynknow.api.pojo.request.AuthFacebookRequest;
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
    public ResponseEntity registerFacebook(AuthFacebookRequest request) {
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
            } else {
                user = new UserData();

                user.setRoleData(role);
                user.setCurrentSubscriptionPackage(subs);
                user.setPassword(encoder.encode(request.getToken()));
                user.setJoinDate(new Date());
                user.setCreatedDate(new Date());

                if (userFb.getEmail() == null) {
                    user.setUsername(userFb.getId() + "@facebook.com");
                    user.setEmail(userFb.getId() + "@facebook.com");
                } else {
                    user.setUsername(userFb.getEmail().toLowerCase());
                    user.setEmail(userFb.getEmail().toLowerCase());
                }

                LOGGER.error("UserFb Token: " + request.getToken());
                LOGGER.error("UserFb Name: " + userFb.getName());
                LOGGER.error("UserFb Firstname: " + userFb.getFirstName());
                LOGGER.error("UserFb Lastname: " + userFb.getLastName());
                if (userFb.getName() == null) {
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

                if (userFb.getName() == null) {
                    profile.setFirstName(userFb.getId() + "@facebook.com");
                    profile.setLastName("");
                } else {
                    profile.setFirstName(userFb.getFirstName());
                    profile.setLastName(userFb.getLastName());
                }

                userProfileRepo.save(profile);
            }

            // auto login after register
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", userFb.getEmail().toLowerCase());
            maps.put("password", request.getToken());

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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
