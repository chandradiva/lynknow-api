package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.UserCardRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserCardResponse;
import com.lynknow.api.repository.*;
import com.lynknow.api.service.CardVerificationService;
import com.lynknow.api.service.UserCardService;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserCardServiceImpl implements UserCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCardServiceImpl.class);

    @Autowired
    private UserCardRepository userCardRepo;

    @Autowired
    private CardTypeRepository cardTypeRepo;

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private CardPhoneDetailRepository cardPhoneDetailRepo;

    @Autowired
    private CardVerificationService cardVerificationService;

    @Autowired
    private NotificationTypeRepository notificationTypeRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private CardRequestViewRepository cardRequestViewRepo;

    @Value("${upload.dir.card.front-side}")
    private String frontSideDir;

    @Value("${upload.dir.card.back-side}")
    private String backSideDir;

    @Value("${upload.dir.card.profile-pic}")
    private String profilePicDir;

    @Value("${contact.vcf.dir}")
    private String contactDir;

    @Override
    public ResponseEntity saveData(UserCardRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            CardType type = cardTypeRepo.getDetail(request.getCardTypeId());
            if (type == null) {
                LOGGER.error("Card Type ID: " + request.getCardTypeId() + " is not found");
                throw new NotFoundException("Card Type ID: " + request.getCardTypeId());
            }

            if (request.getId() == null) {
                // new data
                List<UserCard> cards = userCardRepo.getList(userLogin.getId(), null, null, Sort.by("id").ascending());
                int countCard = cards == null ? 0 : cards.size();
                if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                    // basic
                    if (countCard == 2) {
                        LOGGER.error("You have reached your card limit. User Basic can only have 2 cards");
                        throw new BadRequestException("You have reached your card limit. User Basic can only have 2 cards");
                    }
                } else {
                    // premium
                    if (countCard == 2) {
                        LOGGER.error("You have reached your card limit. User Premium can only have 10 cards");
                        throw new BadRequestException("You have reached your card limit. User Premium can only have 10 cards");
                    }
                }

                UserCard card = new UserCard();

                card.setUserData(userLogin);
                card.setCardType(type);
                card.setFrontSide(request.getFrontSide());
                card.setBackSide(request.getBackSide());
                card.setProfilePhoto(request.getProfilePhoto());
                card.setFirstName(request.getFirstName());
                card.setLastName(request.getLastName());
                card.setDesignation(request.getDesignation());
                card.setCompany(request.getCompany());
                card.setAddress1(request.getAddress1());
                card.setAddress2(request.getAddress2());
                card.setCity(request.getCity());
                card.setPostalCode(request.getPostalCode());
                card.setCountry(request.getCountry());
                card.setEmail(request.getEmail());
                card.setWebsite(request.getWebsite());
                card.setWhatsappNo(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                card.setMobileNo(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                card.setIsPublished(0);
                card.setUniqueCode(UUID.randomUUID().toString());
                card.setVerificationPoint(0);
                card.setCreatedDate(new Date());
                card.setIsActive(1);

                userCardRepo.save(card);

                CardPhoneDetail waDetail;
                CardPhoneDetail mobileDetail;

                Page<CardPhoneDetail> pageWa = cardPhoneDetailRepo.getDetail(card.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
                if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
                    waDetail = pageWa.getContent().get(0);

                    waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                    waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                    waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                } else {
                    waDetail = new CardPhoneDetail();

                    waDetail.setUserCard(card);
                    waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                    waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                    waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                    waDetail.setType(1);
                }

                Page<CardPhoneDetail> pageMobile = cardPhoneDetailRepo.getDetail(card.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
                if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
                    mobileDetail = pageMobile.getContent().get(0);

                    mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                    mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                    mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                } else {
                    mobileDetail = new CardPhoneDetail();

                    mobileDetail.setUserCard(card);
                    mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                    mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                    mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                    mobileDetail.setType(2);
                }

                cardPhoneDetailRepo.save(waDetail);
                cardPhoneDetailRepo.save(mobileDetail);

                // init card verification
                cardVerificationService.initCardVerification(card);
                // end of init card verification

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        201,
                        "Created",
                        generateRes.generateResponseUserCard(card)), HttpStatus.CREATED);
            } else {
                // update data
                UserCard card = userCardRepo.getDetail(request.getId());
                if (card != null) {
                    card.setCardType(type);
                    card.setFrontSide(request.getFrontSide());
                    card.setBackSide(request.getBackSide());
                    card.setProfilePhoto(request.getProfilePhoto());
                    card.setFirstName(request.getFirstName());
                    card.setLastName(request.getLastName());
                    card.setDesignation(request.getDesignation());
                    card.setCompany(request.getCompany());
                    card.setAddress1(request.getAddress1());
                    card.setAddress2(request.getAddress2());
                    card.setCity(request.getCity());
                    card.setPostalCode(request.getPostalCode());
                    card.setCountry(request.getCountry());
                    card.setEmail(request.getEmail());
                    card.setWebsite(request.getWebsite());
                    card.setWhatsappNo(request.getWhatsappNo().getNumber());
                    card.setMobileNo(request.getMobileNo().getNumber());
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);

                    CardPhoneDetail waDetail;
                    CardPhoneDetail mobileDetail;

                    Page<CardPhoneDetail> pageWa = cardPhoneDetailRepo.getDetail(card.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
                    if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
                        waDetail = pageWa.getContent().get(0);

                        waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                        waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                        waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                    } else {
                        waDetail = new CardPhoneDetail();

                        waDetail.setUserCard(card);
                        waDetail.setCountryCode(request.getWhatsappNo().getCountryCode());
                        waDetail.setDialCode(request.getWhatsappNo().getDialCode());
                        waDetail.setNumber(StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                        waDetail.setType(1);
                    }

                    Page<CardPhoneDetail> pageMobile = cardPhoneDetailRepo.getDetail(card.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
                    if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
                        mobileDetail = pageMobile.getContent().get(0);

                        mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                        mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                        mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                    } else {
                        mobileDetail = new CardPhoneDetail();

                        mobileDetail.setUserCard(card);
                        mobileDetail.setCountryCode(request.getMobileNo().getCountryCode());
                        mobileDetail.setDialCode(request.getMobileNo().getDialCode());
                        mobileDetail.setNumber(StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                        mobileDetail.setType(2);
                    }

                    cardPhoneDetailRepo.save(waDetail);
                    cardPhoneDetailRepo.save(mobileDetail);

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            generateRes.generateResponseUserCard(card)), HttpStatus.OK);
                } else {
                    LOGGER.error("User Card ID: " + request.getId() + " is not found");
                    throw new NotFoundException("User Card ID: " + request.getId());
                }
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList(Long userId, Integer typeId, Integer isPublished) {
        try {
            List<UserCardResponse> datas = new ArrayList<>();
            List<UserCard> cards = userCardRepo.getList(
                    userId.equals(0) ? null : userId,
                    typeId == 0 ? null : typeId,
                    isPublished == -1 ? null : isPublished,
                    Sort.by("isPublished").descending());

            if (cards != null) {
                for (UserCard item : cards) {
                    datas.add(generateRes.generateResponseUserCard(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    datas), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPagination(Long userId, Integer typeId, Integer isPublished, PaginationModel model) {
        try {
            Page<UserCardResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = userCardRepo.getListPagination(
                        userId.equals(0) ? null : userId,
                        typeId == 0 ? null : typeId,
                        isPublished == -1 ? null : isPublished,
                        model.getParam().toLowerCase(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy()))
                ).map(generateRes::generateResponseUserCard);
            } else {
                page = userCardRepo.getListPagination(
                        userId.equals(0) ? null : userId,
                        typeId == 0 ? null : typeId,
                        isPublished == -1 ? null : isPublished,
                        model.getParam().toLowerCase(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy()))
                ).map(generateRes::generateResponseUserCard);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity deleteData(Long id) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                card.setIsActive(0);
                card.setDeletedDate(new Date());

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity publishCard(Long id, int type) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                if (type == 1) {
                    // publish
                    card.setIsPublished(1);
                    card.setPublishedDate(new Date());
                } else {
                    // unpublish
                    card.setIsPublished(0);
                    card.setPublishedDate(null);
                }

                card.setUpdatedDate(new Date());

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity uploadFrontSide(MultipartFile file, Long id) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = fileName.replaceAll("\\s+", "_");

        int idx = fileName.lastIndexOf(".");
        String ext = fileName.substring(idx);

        try {
            String newFilename = UUID.randomUUID() + ext;
            File uploadDir = new File(frontSideDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            if (id != null || id != 0) {
                UserCard card = userCardRepo.getDetail(id);
                if (card != null) {
                    card.setFrontSide(newFilename);
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);
                } else {
                    LOGGER.error("User Card ID: " + id + " is not found");
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    newFilename), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            return null;
        }
    }

    @Override
    public ResponseEntity uploadBackSide(MultipartFile file, Long id) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = fileName.replaceAll("\\s+", "_");

        int idx = fileName.lastIndexOf(".");
        String ext = fileName.substring(idx);

        try {
            String newFilename = UUID.randomUUID() + ext;
            File uploadDir = new File(backSideDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            if (id != null || id != 0) {
                UserCard card = userCardRepo.getDetail(id);
                if (card != null) {
                    card.setBackSide(newFilename);
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);
                } else {
                    LOGGER.error("User Card ID: " + id + " is not found");
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    newFilename), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            return null;
        }
    }

    @Override
    public ResponseEntity uploadProfilePicture(MultipartFile file, Long id) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = fileName.replaceAll("\\s+", "_");

        int idx = fileName.lastIndexOf(".");
        String ext = fileName.substring(idx);

        try {
            String newFilename = UUID.randomUUID() + ext;
            File uploadDir = new File(profilePicDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            if (id != null || id != 0) {
                UserCard card = userCardRepo.getDetail(id);
                if (card != null) {
                    card.setProfilePhoto(newFilename);
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);
                } else {
                    LOGGER.error("User Card ID: " + id + " is not found");
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    newFilename), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            return null;
        }
    }

    @Override
    public byte[] getImageFrontSide(String filename, HttpServletResponse httpResponse) throws IOException {
        try {
            File file = new File(frontSideDir + File.separator + filename);
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Front Side Card Image with Filename: " + filename + " is not found");
                throw new NotFoundException("Front Side Card Image with Filename: " + filename);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public byte[] getImageBackSide(String filename, HttpServletResponse httpResponse) throws IOException {
        try {
            File file = new File(backSideDir + File.separator + filename);
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Back Side Card Image with Filename: " + filename + " is not found");
                throw new NotFoundException("Back Side Card Image with Filename: " + filename);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public byte[] getImageProfilePicture(String filename, HttpServletResponse httpResponse) throws IOException {
        try {
            File file = new File(profilePicDir + File.separator + filename);
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Profile Picture Card Image with Filename: " + filename + " is not found");
                throw new NotFoundException("Profile Picture Card Image with Filename: " + filename);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public byte[] getImageCard(String filename, HttpServletResponse httpResponse) throws IOException {
        try {
            File file = new File(frontSideDir + File.separator + filename);
            if (file.exists()) {
                httpResponse.setContentType("image/*");
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                FileInputStream fis = new FileInputStream(file);

                return IOUtils.toByteArray(fis);
            } else {
                file = new File(backSideDir + File.separator + filename);
                if (file.exists()) {
                    httpResponse.setContentType("image/*");
                    httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    FileInputStream fis = new FileInputStream(file);

                    return IOUtils.toByteArray(fis);
                } else {
                    file = new File(profilePicDir + File.separator + filename);
                    if (file.exists()) {
                        httpResponse.setContentType("image/*");
                        httpResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
                        httpResponse.setStatus(HttpServletResponse.SC_OK);
                        FileInputStream fis = new FileInputStream(file);

                        return IOUtils.toByteArray(fis);
                    } else {
                        LOGGER.error("Card Image with Filename: " + filename + " is not found");
                        throw new NotFoundException("Card Image with Filename: " + filename);
                    }
                }
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailByCode(String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = null;
            if (!(auth.getPrincipal() instanceof String) && !auth.getPrincipal().equals("anonymousUser")) {
                userSession = (UserData) auth.getPrincipal();
            }

            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if (card.getUserData().getCurrentSubscriptionPackage().getId() == 2
                        && card.getIsCardLocked() == 1) {
                    // card locked
                    if (userSession != null) {
                        CardRequestView request = cardRequestViewRepo.getDetail(card.getId(), userSession.getId());
                        if (request.getIsGranted() == 1) {
                            return new ResponseEntity(new BaseResponse<>(
                                    true,
                                    200,
                                    "Success",
                                    generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
                        }
                    }

                    LOGGER.error("Card Locked by Users. Users must Request to View Card");
                    throw new UnprocessableEntityException("Card Locked by Users. Users must Request to View Card");
                }

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card Code: " + code + " is not found");
                throw new NotFoundException("User Card Code: " + code);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity lockCard(Long id, int type) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                LOGGER.error("Only Premium Users that can Lock Their Cards");
                throw new BadRequestException("Only Premium Users that can Lock Their Cards");
            }

            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                if (type == 1) {
                    // lock card
                    card.setIsCardLocked(1);
                } else {
                    // unlock card
                    card.setIsCardLocked(0);
                }

                card.setUpdatedDate(new Date());

                userCardRepo.save(card);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity checkCreateCard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            List<UserCard> cards = userCardRepo.getList(userLogin.getId(), null, null, Sort.by("id").ascending());
            int countCard = cards == null ? 0 : cards.size();
            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                if (countCard == 2) {
                    LOGGER.error("You have reached your card limit. User Basic can only have 2 cards");
                    throw new BadRequestException("You have reached your card limit. User Basic can only have 2 cards");
                }
            } else {
                // premium
                if (countCard == 2) {
                    LOGGER.error("You have reached your card limit. User Premium can only have 10 cards");
                    throw new BadRequestException("You have reached your card limit. User Premium can only have 10 cards");
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    null), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id, int typeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            // 1 = profile pic
            // 2 = mobile number
            // 3 = sms
            // 4 = whatsapp
            // 5 = email
            // 6 = exchange card
            // 7 = view detail card
            // 8 = download contact
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                NotificationType type = notificationTypeRepo.getDetail(typeId);

                Notification notification = new Notification();

                notification.setUserData(userLogin);
                notification.setTargetUserData(card.getUserData());
                notification.setNotificationType(type);
                notification.setIsRead(0);
                notification.setCreatedDate(new Date());
                notification.setIsActive(1);

                notificationRepo.save(notification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCard(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public byte[] downloadContact(Long id, HttpServletResponse httpResponse) throws IOException {
        BufferedWriter writer = null;

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                NotificationType type = notificationTypeRepo.getDetail(8);

                Notification notification = new Notification();

                notification.setUserData(userLogin);
                notification.setTargetUserData(card.getUserData());
                notification.setNotificationType(type);
                notification.setIsRead(0);
                notification.setCreatedDate(new Date());
                notification.setIsActive(1);

                notificationRepo.save(notification);
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }

            String filename = new Date().getTime() + "_" + UUID.randomUUID().toString() + ".vcf";
            File contactFolder = new File( contactDir);
            if (!contactFolder.exists()) {
                contactFolder.mkdirs();
            }

            String data = "BEGIN:VCARD\n" +
                    "VERSION:4.0\n" +
                    "N:Gump;Forrest;;Mr.;\n" +
                    "FN:Forrest Gump\n" +
                    "ORG:Bubba Gump Shrimp Co.\n" +
                    "TITLE:Shrimp Man\n" +
                    "PHOTO;MEDIATYPE=image/gif:http://www.example.com/dir_photos/my_photo.gif\n" +
                    "TEL;TYPE=work,voice;VALUE=uri:tel:+1-111-555-1212\n" +
                    "TEL;TYPE=home,voice;VALUE=uri:tel:+1-404-555-1212\n" +
                    "ADR;TYPE=WORK;PREF=1;LABEL=\"100 Waters Edge\\nBaytown\\, LA 30314\\nUnited States of America\":;;100 Waters Edge;Baytown;LA;30314;United States of America\n" +
                    "ADR;TYPE=HOME;LABEL=\"42 Plantation St.\\nBaytown\\, LA 30314\\nUnited States of America\":;;42 Plantation St.;Baytown;LA;30314;United States of America\n" +
                    "EMAIL:forrestgump@example.com\n" +
                    "REV:20080424T195243Z\n" +
                    "x-qq:21588891\n" +
                    "END:VCARD";

            writer = new BufferedWriter(new FileWriter(contactFolder.getAbsoluteFile() + File.separator + filename));
            writer.write(data);

            httpResponse.setContentType("text/vcard");
            httpResponse.setHeader("Content-Disposition", "attachment; filename=" + filename);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            FileInputStream fis = new FileInputStream(new File(contactFolder + File.separator + filename));

            return IOUtils.toByteArray(fis);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public ResponseEntity requestToViewCard(String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if (card.getIsCardLocked() == 0) {
                    LOGGER.error("You Can't Request to View Card, Because is not Locked");
                    throw new UnprocessableEntityException("You Can't Request to View Card, Because is not Locked");
                }

                // insert to table request to view card
                CardRequestView request = cardRequestViewRepo.getDetail(card.getId(), userLogin.getId());
                if (request == null) {
                    request = new CardRequestView();

                    request.setExpiredRequestDate(new Date());
                    request.setCreatedDate(new Date());
                } else {
                    request.setUpdatedDate(new Date());
                }

                request.setUserCard(card);
                request.setUserData(userLogin);
                request.setIsGranted(0);
                request.setIsActive(1);

                cardRequestViewRepo.save(request);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
            } else {
                LOGGER.error("User Card Code: " + code + " is not found");
                throw new NotFoundException("User Card Code: " + code);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity grantRequest(Long requestId, int flag) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            CardRequestView request = cardRequestViewRepo.getDetail(requestId);
            if (request != null) {
                if (!userSession.getId().equals(request.getUserCard().getUserData().getId())) {
                    LOGGER.error("You Can't Grant / Reject Request to View Other User Card");
                    throw new BadRequestException("You Can't Grant / Reject Request to View Other User Card");
                }

                if (flag == 1) {
                    request.setIsGranted(1);
                } else {
                    request.setIsGranted(0);
                    request.setIsActive(0);
                }

                request.setExpiredRequestDate(null);
                request.setUpdatedDate(new Date());

                cardRequestViewRepo.save(request);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Card Request View ID: " + requestId + " is not found");
                throw new NotFoundException("Card Request View ID: " + requestId);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailLockedCard(Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                CardRequestView request = cardRequestViewRepo.getDetail(id, userLogin.getId());
                if (request != null) {
                    if (request.getIsGranted() == 1) {
                        return new ResponseEntity(new BaseResponse<>(
                                true,
                                200,
                                "Success",
                                generateRes.generateResponseUserCard(card)), HttpStatus.OK);
                    } else {
                        LOGGER.error("Your Request to View Card not yet Granted");
                        throw new UnprocessableEntityException("Your Request to View Card not yet Granted");
                    }
                } else {
                    LOGGER.error("You Haven't Request to View Card yet");
                    throw new UnprocessableEntityException("You Haven't Request to View Card yet");
                }
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
