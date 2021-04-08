package com.lynknow.api.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.model.UserPhoneDetail;
import com.lynknow.api.model.UserProfile;
import com.lynknow.api.pojo.request.AuthSocialRequest;
import com.lynknow.api.pojo.request.UserProfileRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.repository.UserPhoneDetailRepository;
import com.lynknow.api.repository.UserProfileRepository;
import com.lynknow.api.service.AWSS3Service;
import com.lynknow.api.service.UserProfileService;
import com.lynknow.api.util.GenerateResponseUtil;
import com.lynknow.api.util.StringUtil;
import org.apache.commons.io.IOUtils;
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
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private UserPhoneDetailRepository userPhoneDetailRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private AWSS3Service awss3Service;

    @Value("${upload.dir.user.profile-pic}")
    private String profilePicDir;

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public ResponseEntity updateProfile(UserProfileRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());
            if (profile == null) {
                profile = new UserProfile();

                profile.setIsEmailVerified(0);
                profile.setIsWhatsappNoVerified(0);
                profile.setCreatedDate(new Date());
                profile.setIsActive(1);
            } else {
                profile.setUpdatedDate(new Date());
            }

            profile.setUserData(userSession);
            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setAddress1(request.getAddress1());
            profile.setAddress2(request.getAddress2());
            profile.setCountry(request.getCountry());
            profile.setWhatsappNo(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
            profile.setMobileNo(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
            profile.setCity(request.getCity());
            profile.setPostalCode(request.getPostalCode());

            userProfileRepo.save(profile);

            // update user data
            UserData user = userDataRepo.getDetail(userSession.getId());

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            userDataRepo.save(user);
            // end of update user data

            UserPhoneDetail waDetail;
            UserPhoneDetail mobileDetail;

            Page<UserPhoneDetail> pageWa = userPhoneDetailRepo.getDetail(profile.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
            if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
                waDetail = pageWa.getContent().get(0);

                waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
            } else {
                waDetail = new UserPhoneDetail();

                waDetail.setUserProfile(profile);
                waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                waDetail.setType(1);
            }

            Page<UserPhoneDetail> pageMobile = userPhoneDetailRepo.getDetail(profile.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
            if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
                mobileDetail = pageMobile.getContent().get(0);

                mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
            } else {
                mobileDetail = new UserPhoneDetail();

                mobileDetail.setUserProfile(profile);
                mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                mobileDetail.setType(2);
            }

            userPhoneDetailRepo.save(waDetail);
            userPhoneDetailRepo.save(mobileDetail);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());
            if (profile != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseProfile(profile)), HttpStatus.OK);
            } else {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity uploadProfilePicture(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = fileName.replaceAll("\\s+", "_");

        int idx = fileName.lastIndexOf(".");
        String ext = fileName.substring(idx);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            String newFilename = UUID.randomUUID() + ext;
            File uploadDir = new File(profilePicDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            if (profile != null) {
                profile.setProfilePhoto(newFilename);
                profile.setUpdatedDate(new Date());
            } else {
                profile = new UserProfile();

                profile.setUserData(userLogin);
                profile.setFirstName(userLogin.getFirstName());
                profile.setLastName(userLogin.getLastName());
                profile.setProfilePhoto(newFilename);
                profile.setIsEmailVerified(0);
                profile.setIsWhatsappNoVerified(0);
                profile.setCreatedDate(new Date());
                profile.setIsActive(1);
            }

            userProfileRepo.save(profile);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity uploadProfilePictureAws(MultipartFile file) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            String url = awss3Service.uploadFile(file);
            if (profile != null) {
                profile.setProfilePhoto(url);
                profile.setUpdatedDate(new Date());
            } else {
                profile = new UserProfile();

                profile.setUserData(userLogin);
                profile.setFirstName(userLogin.getFirstName());
                profile.setLastName(userLogin.getLastName());
                profile.setProfilePhoto(url);
                profile.setIsEmailVerified(0);
                profile.setIsWhatsappNoVerified(0);
                profile.setCreatedDate(new Date());
                profile.setIsActive(1);
            }

            userProfileRepo.save(profile);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public byte[] getProfilePhoto(HttpServletResponse httpResponse) throws IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            File file = new File(profilePicDir + File.separator + profile.getProfilePhoto());
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + profile.getProfilePhoto());
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Profile Picture with Filename: " + profile.getProfilePhoto() + " is not found");
                throw new NotFoundException("Profile Picture with Filename: " + profile.getProfilePhoto());
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity connectFacebook(AuthSocialRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            Facebook fb = new FacebookTemplate(request.getToken(), "", facebookAppId);
            String[] fields = {"id", "email", "first_name", "last_name", "gender", "birthday"};
            User userFb = fb.fetchObject("me", User.class, fields);

            // update profile
            profile.setFbId(userFb.getId());
            profile.setUpdatedDate(new Date());

            userProfileRepo.save(profile);
            // end of update profile

            // update verification point user
            UserData user = profile.getUserData();

            if (profile.getGoogleId() == null) {
                user.setVerificationPoint(user.getVerificationPoint() + 20);
                user.setUpdatedDate(new Date());

                userDataRepo.save(user);
            }
            // end of update verification point user

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    generateRes.generateResponseProfile(profile)), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity connectGoogle(AuthSocialRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserProfile profile = userProfileRepo.getDetailByUserId(userSession.getId());

            NetHttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // update profile
                profile.setGoogleId(payload.getSubject());
                profile.setUpdatedDate(new Date());

                userProfileRepo.save(profile);
                // end of update profile

                // update verification point user
                UserData user = profile.getUserData();

                if (profile.getFbId() == null) {
                    user.setVerificationPoint(user.getVerificationPoint() + 20);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);
                }
                // end of update verification point user

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseProfile(profile)), HttpStatus.OK);
            } else {
                LOGGER.error("Failed to Get Detail User");
                throw new NotFoundException("Failed to Get Detail User", 404);
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
        }
    }

}
