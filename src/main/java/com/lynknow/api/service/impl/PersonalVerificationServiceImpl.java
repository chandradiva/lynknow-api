package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.PersonalVerification;
import com.lynknow.api.model.PersonalVerificationItem;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.VerifyPersonalRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.PersonalVerificationResponse;
import com.lynknow.api.pojo.response.UserDataResponse;
import com.lynknow.api.repository.PersonalVerificationItemRepository;
import com.lynknow.api.repository.PersonalVerificationRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.PersonalVerificationService;
import com.lynknow.api.util.GenerateResponseUtil;
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
import java.util.*;

@Service
public class PersonalVerificationServiceImpl implements PersonalVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalVerificationServiceImpl.class);

    @Autowired
    private PersonalVerificationItemRepository itemRepo;

    @Autowired
    private PersonalVerificationRepository personalVerificationRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Value("${upload.dir.user.verification}")
    private String verificationDir;

    @Override
    public void initVerification(UserData user) {
        try {
            List<PersonalVerification> verifications = personalVerificationRepo.getList(user.getId());
            if (verifications == null || verifications.size() == 0) {
                PersonalVerificationItem itemGovId = itemRepo.getDetail(1);
                PersonalVerificationItem itemEmpDoc = itemRepo.getDetail(2);

                PersonalVerification verification = new PersonalVerification();
                verification.setUserData(user);
                verification.setPersonalVerificationItem(itemGovId);
                verification.setIsVerified(0);
                verification.setIsRequested(0);
                verification.setCreatedDate(new Date());
                personalVerificationRepo.save(verification);

                verification = new PersonalVerification();
                verification.setUserData(user);
                verification.setPersonalVerificationItem(itemEmpDoc);
                verification.setIsVerified(0);
                verification.setIsRequested(0);
                verification.setCreatedDate(new Date());
                personalVerificationRepo.save(verification);
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

    @Override
    public ResponseEntity initVerification() {
        return null;
    }

    @Override
    public ResponseEntity requestToVerify(MultipartFile file, String remarks, Integer itemId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                LOGGER.error("Only Premium Users that can Verify Their Personal Data");
                throw new BadRequestException("Only Premium Users that can Verify Their Personal Data");
            }

            // init verification
            initVerification(userLogin);
            // end of init verification

            PersonalVerification verification = personalVerificationRepo.getDetail(userLogin.getId(), itemId);
            if (verification != null) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                fileName = fileName.replaceAll("\\s+", "_");

                int idx = fileName.lastIndexOf(".");
                String ext = fileName.substring(idx);

                String newFilename = UUID.randomUUID() + ext;
                File uploadDir = new File(verificationDir);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // update status
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 3);

                PersonalVerificationItem item = itemRepo.getDetail(itemId);
                if (item == null) {
                    LOGGER.error("Personal Verification Item ID: " + itemId + " is not found");
                    throw new NotFoundException("Personal Verification Item ID: " + itemId);
                }

                verification.setUserData(userLogin);
                verification.setPersonalVerificationItem(item);
                verification.setRemarks(remarks);
                verification.setParam(newFilename);
                verification.setIsRequested(1);
                verification.setUpdatedDate(new Date());

                personalVerificationRepo.save(verification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponsePersonalVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Personal Verification with User ID: " + userLogin.getId() + " and Item ID: " + itemId + " is not found");
                throw new NotFoundException("Personal Verification with User ID: " + userLogin.getId() + " and Item ID: " + itemId);
            }
        } catch (InternalServerErrorException | IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity verifyRequest(VerifyPersonalRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can Verify Personal Verification");
                throw new BadRequestException("Only Administrator Roles That Can Verify Personal Verification");
            }

            PersonalVerification verification = personalVerificationRepo.getDetail(request.getUserId(), request.getItemId());
            if (verification != null) {
                if (verification.getIsRequested() == 0) {
                    LOGGER.error("The User is Not Yet Requested for Personal Verification");
                    throw new UnprocessableEntityException("The User is Not Yet Requested for Personal Verification");
                }

                if (request.getVerify() == 1) {
                    verification.setIsVerified(1);
                    verification.setVerifiedBy(userLogin);
                    verification.setVerifiedDate(new Date());

                    userLogin.setVerificationPoint(userLogin.getVerificationPoint() + 20);
                    userDataRepo.save(userLogin);
                } else {
                    verification.setIsVerified(0);
                    verification.setReason(request.getReason());
                }

                verification.setIsRequested(0);
                verification.setUpdatedDate(new Date());

                personalVerificationRepo.save(verification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponsePersonalVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Personal Verification with User ID: " + request.getUserId() + " and Item ID: " + request.getItemId() + " is not found");
                throw new NotFoundException("Personal Verification with User ID: " + request.getUserId() + " and Item ID: " + request.getItemId());
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            PersonalVerification verification = personalVerificationRepo.getDetail(id);
            if (verification != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponsePersonalVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Personal Verification ID: " + id + " is not found");
                throw new NotFoundException("Personal Verification ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long userId, Integer itemId) {
        try {
            PersonalVerification verification = personalVerificationRepo.getDetail(userId, itemId);
            if (verification != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponsePersonalVerification(verification)), HttpStatus.OK);
            } else {
                LOGGER.error("Personal Verification with User ID: " + userId + " and Item ID: " + itemId + " is not found");
                throw new NotFoundException("Personal Verification with User ID: " + userId + " and Item ID: " + itemId);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList(Long userId) {
        try {
            List<PersonalVerificationResponse> res = new ArrayList<>();
            List<PersonalVerification> verifications = personalVerificationRepo.getList(userId);
            if (verifications != null) {
                for (PersonalVerification item : verifications) {
                    res.add(generateRes.generateResponsePersonalVerification(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    res), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListNeedToVerify(PaginationModel model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Only Administrator Roles That Can View Request for Personal Verification");
                throw new BadRequestException("Only Administrator Roles That Can View Request for Personal Verification");
            }

            Page<UserDataResponse> page;
            model.setSortBy("ud." + model.getSortBy());

            if (model.getSort().equals("asc")) {
                page = userDataRepo.getListNeedVerify(PageRequest.of(
                        model.getPage(),
                        model.getSize(),
                        Sort.by(model.getSortBy()).ascending())).map(generateRes::generateResponseUser);
            } else {
                page = userDataRepo.getListNeedVerify(PageRequest.of(
                        model.getPage(),
                        model.getSize(),
                        Sort.by(model.getSortBy()).descending())).map(generateRes::generateResponseUser);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public byte[] getData(String filename, HttpServletResponse httpResponse) throws IOException {
        try {
            File file = new File(verificationDir + File.separator + filename);
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Personal Verification Data with Filename: " + filename + " is not found");
                throw new NotFoundException("Personal Verification Data with Filename: " + filename);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
