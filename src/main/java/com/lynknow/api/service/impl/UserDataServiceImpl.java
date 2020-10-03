package com.lynknow.api.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lynknow.api.exception.*;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.request.*;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.AuthService;
import com.lynknow.api.service.UserDataService;
import com.lynknow.api.service.UserOtpService;
import com.lynknow.api.util.EmailUtil;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

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

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private UsedReferralCodeRepository usedReferralCodeRepo;

    @Autowired
    private UserOtpService userOtpService;

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${fe.url.reset-password}")
    private String resetPasswordUrl;

    @Value("${fe.url.verify-change-email}")
    private String verifyChangeEmailUrl;

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

            this.addReferralCode(user, request.getReferralCode());

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

                this.addReferralCode(user, request.getReferralCode());

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

                    this.addReferralCode(user, request.getReferralCode());

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

    @Override
    public ResponseEntity forgotPassword(String email) {
        try {
            Page<UserData> page = userDataRepo.getByEmail(
                    email.toLowerCase(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (page.getContent() != null && page.getContent().size() > 0) {
                UserData user = page.getContent().get(0);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 3);

                user.setAccessToken(UUID.randomUUID().toString());
                user.setExpiredToken(cal.getTime());

                userDataRepo.save(user);

                // send email
                String url = resetPasswordUrl + user.getAccessToken();
                emailUtil.sendEmail(
                        user.getEmail(),
                        "Lynknow - Forgot Password",
                        "Please click url below to Reset your Password: <br/><br/> <b><a href=\"" + url + "\">Reset Password</a></b>");
                // end of send email

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Your Email Address: " + email + " is not found");
                throw new NotFoundException("Your Email Address: " + email);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity checkToken(String token) {
        try {
            Page<UserData> pageUser = userDataRepo.getByAccessToken(
                    token,
                    PageRequest.of(0, 1, Sort.by("id").descending())
            );

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                UserData user = pageUser.getContent().get(0);

                if (user.getExpiredToken().after(new Date())) {
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            null), HttpStatus.OK);
                } else {
                    user.setAccessToken(null);
                    user.setExpiredToken(null);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    LOGGER.error("Your Forgot Password Token is Expired");
                    throw new UnprocessableEntityException("Your Forgot Password Token is Expired");
                }
            } else {
                LOGGER.error("Invalid Access Token");
                throw new UnprocessableEntityException("Invalid Access Token");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity resetPassword(ResetPasswordRequest request) {
        try {
            Page<UserData> pageUser = userDataRepo.getByAccessToken(
                    request.getAccessToken(),
                    PageRequest.of(0, 1, Sort.by("id").descending())
            );

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                UserData user = pageUser.getContent().get(0);

                if (user.getExpiredToken().after(new Date())) {
                    user.setPassword(encoder.encode(request.getPassword()));
                    user.setAccessToken(null);
                    user.setExpiredToken(null);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            null), HttpStatus.OK);
                } else {
                    user.setAccessToken(null);
                    user.setExpiredToken(null);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    LOGGER.error("Your Forgot Password Token is Expired");
                    throw new UnprocessableEntityException("Your Forgot Password Token is Expired");
                }
            } else {
                LOGGER.error("Invalid Access Token");
                throw new UnprocessableEntityException("Invalid Access Token");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity checkIsEmailVerified() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            boolean verified = false;
            if (profile.getIsEmailVerified() == 1) {
                verified = true;
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    verified), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity changeEmail(ChangeEmailRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (!request.getOldEmail().equalsIgnoreCase(userLogin.getEmail())) {
                LOGGER.error("Old Email: " + request.getOldEmail() + " does not match with your current email");
                throw new BadRequestException("Old Email: " + request.getOldEmail() + " does not match with your current email");
            }

            if (!this.checkByUsername(request.getNewEmail(), null)) {
                LOGGER.error("Email: " + request.getNewEmail() + " already exist");
                throw new ConflictException("Email: " + request.getNewEmail() + " already exist");
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, 3);

            userLogin.setTempEmail(request.getNewEmail());
            userLogin.setUpdatedDate(new Date());
            userLogin.setAccessToken(UUID.randomUUID().toString());
            userLogin.setExpiredToken(cal.getTime());

            userDataRepo.save(userLogin);

            // send email
            String url = verifyChangeEmailUrl + userLogin.getAccessToken();
            emailUtil.sendEmail(
                    userLogin.getEmail(),
                    "Lynknow - Verify Change Email",
                    "Please click url below to Verify Change Email: <br/><br/> <b><a href=\"" + url + "\">Verify</a></b>");
            // end of send email

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyChangeEmail(String token) {
        try {
            Page<UserData> pageUser = userDataRepo.getByAccessToken(
                    token,
                    PageRequest.of(0, 1, Sort.by("id").descending())
            );

            if (pageUser.getContent() != null && pageUser.getContent().size() > 0) {
                UserData user = pageUser.getContent().get(0);

                user.setEmail(user.getTempEmail());
                user.setUsername(user.getTempEmail());
                user.setTempEmail(null);
                user.setAccessToken(null);
                user.setExpiredToken(null);
                user.setVerificationPoint(user.getVerificationPoint() - 20);
                user.setUpdatedDate(new Date());

                userDataRepo.save(user);

                UserProfile profile = userProfileRepo.getDetailByUserId(user.getId());

                profile.setIsEmailVerified(0);
                profile.setUpdatedDate(new Date());

                userProfileRepo.save(profile);

//                userOtpService.verifyEmail();

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Invalid Access Token");
                throw new UnprocessableEntityException("Invalid Access Token");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("This API is for Administrator Only");
                throw new BadRequestException("This API is for Administrator Only");
            }

            UserData user = userDataRepo.getDetail(id);
            if (user != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserComplete(user)), HttpStatus.OK);
            } else {
                LOGGER.error("User ID: " + id + " is not found");
                throw new NotFoundException("User ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity changePassword(ChangePasswordRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (!encoder.matches(request.getOldPassword(), userLogin.getPassword())) {
                LOGGER.error("Old Password does not match with your current Password");
                throw new BadRequestException("Old Password does not match with your current Password");
            }

            if (!request.getNewPassword().equalsIgnoreCase(request.getConfirmNewPassword())) {
                LOGGER.error("New Password does not match with New Confirm Password");
                throw new BadRequestException("New Password does not match with New Confirm Password");
            }

            userLogin.setPassword(encoder.encode(request.getNewPassword()));
            userLogin.setUpdatedDate(new Date());

            userDataRepo.save(userLogin);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity updateExpiredTotalView() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("This API is for Administrator Only");
                throw new BadRequestException("This API is for Administrator Only");
            }

            List<UserData> users = userDataRepo.getListAll();
            if (users.size() > 0) {
                for (UserData item : users) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.getJoinDate());
                    cal.add(Calendar.YEAR, 1);

                    item.setExpiredTotalView(cal.getTime());

                    userDataRepo.save(item);
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public void resetToBasic(UserData user) {
        try {
            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(1); // basic plan

            user.setCurrentSubscriptionPackage(subs);
            user.setMaxVerificationCredit(0);
            user.setCurrentVerificationCredit(0);
            user.setExpiredPremiumDate(null);
            user.setUpdatedDate(new Date());

            userDataRepo.save(user);

            // reset locked card
            List<UserCard> cards = userCardRepo.getList(user.getId(), null, null, Sort.unsorted());
            if (cards != null) {
                for (UserCard item : cards) {
                    item.setIsCardLocked(0);
                    item.setUpdatedDate(new Date());

                    userCardRepo.save(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void addReferralCode(UserData user, String referralCode) {
        if (referralCode != null && !referralCode.equals("")) {
            UserCard card = userCardRepo.getByUniqueCode(referralCode);
            if (card == null) {
                LOGGER.error("User Card Code: " + referralCode + " is not found");
//                throw new NotFoundException("User Card Code: " + referralCode);
            } else {
                UsedReferralCode referral = usedReferralCodeRepo.getDetail(user.getId(), referralCode);
                if (referral == null) {
                    referral = new UsedReferralCode();

                    referral.setUserData(user);
                    referral.setReferralUserCard(card);
                    referral.setReferralCode(referralCode);
                    referral.setAdditionalView(500);
                    referral.setCreatedDate(new Date());
                    referral.setIsActive(1);

                    usedReferralCodeRepo.save(referral);

                    // update max total view
                    UserData referralUser = card.getUserData();

                    if (referralUser.getMaxTotalView() < 20000) {
                        referralUser.setMaxTotalView(referralUser.getMaxTotalView() + 500);
                        referralUser.setUpdatedDate(new Date());
                    }

                    userDataRepo.save(referralUser);
                    // end of update max total view
                }
            }
        }
    }

}
