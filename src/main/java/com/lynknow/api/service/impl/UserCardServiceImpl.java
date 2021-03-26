package com.lynknow.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.lynknow.api.service.CardViewersService;
import com.lynknow.api.service.UserCardService;
import com.lynknow.api.service.UserContactService;
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

    @Autowired
    private UserContactRepository userContactRepo;

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private UserContactService userContactService;

    @Autowired
    private CardViewersService cardViewersService;

    @Value("${upload.dir.card.front-side}")
    private String frontSideDir;

    @Value("${upload.dir.card.back-side}")
    private String backSideDir;

    @Value("${upload.dir.card.profile-pic}")
    private String profilePicDir;

    @Value("${upload.dir.user.profile-pic}")
    private String userProfilePicDir;

    @Value("${contact.vcf.dir}")
    private String contactDir;

    @Override
    public ResponseEntity saveData(UserCardRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            ObjectMapper mapper = new ObjectMapper();

            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());
            if (profile.getIsEmailVerified() == 0) {
                LOGGER.error("Your Email has not been Verified");
                throw new BadRequestException("Your Email has not been Verified");
            }

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
                    if (countCard == 10) {
                        LOGGER.error("You have reached your card limit. User Pro can only have 10 cards");
                        throw new BadRequestException("You have reached your card limit. User Pro can only have 10 cards");
                    }
                }

                // generate unique code
                int i = 0;
                String uniqueCode = "";
                do {
                    if (i == 50) {
                        LOGGER.error("Failed to Generate Unique Code Card, Please Try Again Later");
                        throw new BadRequestException("Failed to Generate Unique Code Card, Please Try Again Later");
                    }

                    uniqueCode = StringUtil.generateUniqueCodeCard();
                    i++;
                } while (userCardRepo.getByUniqueCode(uniqueCode) != null);
                // end of generate unique code

                UserCard card = new UserCard();

                if (request.getIsLock() == 1) {
                    if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                        LOGGER.error("Only Pro Users that can Lock Their Cards");
                        throw new BadRequestException("Only Pro Users that can Lock Their Cards");
                    }

                    card.setIsCardLocked(1);
                } else {
                    card.setIsCardLocked(0);
                }

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
                card.setWhatsappNo(request.getWhatsappNo() == null ? null : StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                card.setMobileNo(request.getMobileNo() == null ? null : StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                card.setIsPublished(0);
                card.setUniqueCode(uniqueCode);
                card.setVerificationPoint(0);
                card.setCreatedDate(new Date());
                card.setIsActive(1);
                card.setSocials(mapper.writeValueAsString(request.getSocials()));

                if (request.getOtherMobileNo() != null && request.getOtherMobileNo().size() > 0) {
                    String phones = mapper.writeValueAsString(request.getOtherMobileNo());

                    card.setOtherMobileNo(phones);
                }

                userCardRepo.save(card);

                CardPhoneDetail waDetail;
                CardPhoneDetail mobileDetail;

                if (request.getWhatsappNo() != null) {
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

                    cardPhoneDetailRepo.save(waDetail);
                }

                if (request.getMobileNo() != null) {
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

                    cardPhoneDetailRepo.save(mobileDetail);
                }

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
                    if (request.getIsLock() == 1) {
                        if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                            LOGGER.error("Only Pro Users that can Lock Their Cards");
                            throw new BadRequestException("Only Pro Users that can Lock Their Cards");
                        }

                        card.setIsCardLocked(1);
                    } else {
                        card.setIsCardLocked(0);
                    }

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
                    card.setWhatsappNo(request.getWhatsappNo() == null ? null : StringUtil.normalizePhoneNumber(request.getWhatsappNo().getNumber()));
                    card.setMobileNo(request.getMobileNo() == null ? null : StringUtil.normalizePhoneNumber(request.getMobileNo().getNumber()));
                    card.setSocials(mapper.writeValueAsString(request.getSocials()));
                    card.setUpdatedDate(new Date());

                    if (request.getOtherMobileNo() != null && request.getOtherMobileNo().size() > 0) {
                        String phones = mapper.writeValueAsString(request.getOtherMobileNo());

                        card.setOtherMobileNo(phones);
                    } else {
                        card.setOtherMobileNo(null);
                    }

                    userCardRepo.save(card);

                    CardPhoneDetail waDetail;
                    CardPhoneDetail mobileDetail;

                    if (request.getWhatsappNo() != null) {
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

                        cardPhoneDetailRepo.save(waDetail);
                    }

                    if (request.getMobileNo() != null) {
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

                        cardPhoneDetailRepo.save(mobileDetail);
                    }

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
        } catch (InternalServerErrorException | JsonProcessingException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            UserData userSession = (UserData) auth.getPrincipal();
//            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserCard card = userCardRepo.getDetail(id);
//            if (!userLogin.getId().equals(card.getUserData().getId())) {
//                return getDetail(id, 10);
//            }

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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

                // notify updated card to contact
//                if (card.getUpdatedDate() != null) {
//                    new Thread(() -> {
//                        userContactService.notifyUpdatedCard(card);
//                    }).start();
//                }
                // end of notify updated card to contact

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                    if (card.getFrontSide() != null) {
                        File existingImage = new File(frontSideDir + File.separator + card.getFrontSide());
                        if (existingImage.exists() && existingImage.isFile()) {
                            existingImage.delete();
                        }
                    }

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                    if (card.getBackSide() != null) {
                        File existingImage = new File(backSideDir + File.separator + card.getBackSide());
                        if (existingImage.exists() && existingImage.isFile()) {
                            existingImage.delete();
                        }
                    }

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                    if (card.getProfilePhoto() != null) {
                        File existingImage = new File(profilePicDir + File.separator + card.getProfilePhoto());
                        if (existingImage.exists() && existingImage.isFile()) {
                            existingImage.delete();
                        }
                    }

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                        file = new File(userProfilePicDir + File.separator + filename);
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
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailByCode(String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = null;
            UserData userLogin = null;

            if (!(auth.getPrincipal() instanceof String) && !auth.getPrincipal().equals("anonymousUser")) {
                userSession = (UserData) auth.getPrincipal();
                userLogin = userDataRepo.getDetail(userSession.getId());
            }

            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if ((card.getUserData().getCurrentSubscriptionPackage().getId().equals(2)
                        || card.getUserData().getCurrentSubscriptionPackage().getId().equals(3))
                        && card.getIsCardLocked() == 1
                ) {
                    // card locked
                    if (userSession != null && userLogin != null) {
                        UserData user = card.getUserData();
                        if (!userLogin.getId().equals(user.getId())) {
                            // set used total view
                            user.setUsedTotalView(user.getUsedTotalView() + 1);
                            user.setUpdatedDate(new Date());

                            userDataRepo.save(user);
                            // end of set used total view

                            // save notification data
                            NotificationType type = notificationTypeRepo.getDetail(10); // view card

                            Notification notification = new Notification();

                            notification.setUserData(userLogin);
                            notification.setTargetUserData(card.getUserData());
                            notification.setTargetUserCard(card);
                            notification.setNotificationType(type);
                            notification.setIsRead(0);
                            notification.setCreatedDate(new Date());
                            notification.setIsActive(1);

                            notificationRepo.save(notification);
                            // end of save notification data
                        }

                        if (userLogin.getId().equals(card.getUserData().getId())) {
                            return new ResponseEntity(new BaseResponse<>(
                                    true,
                                    200,
                                    "Success",
                                    generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
                        }

                        if (userLogin.getVerificationPoint() > 50) {
                            return new ResponseEntity(new BaseResponse<>(
                                    true,
                                    200,
                                    "Success",
                                    generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
                        }

                        CardRequestView request = cardRequestViewRepo.getDetail(card.getId(), userSession.getId());
                        if (request != null && request.getIsGranted() == 1) {
                            return new ResponseEntity(new BaseResponse<>(
                                    true,
                                    200,
                                    "Success",
                                    generateRes.generateResponseUserCardPublic(card)), HttpStatus.OK);
                        }
                    }

                    LOGGER.error("Card Locked by Users. Users must Request to View Card");
                    throw new UnprocessableEntityException("Card Locked by Users. Users must Request to View Card");
                } else {
                    // card not locked
                    UserData user = card.getUserData();
                    if (userLogin != null && !userLogin.getId().equals(user.getId())) {
                        // set used total view
                        user.setUsedTotalView(user.getUsedTotalView() + 1);
                        user.setUpdatedDate(new Date());

                        userDataRepo.save(user);
                        // end of set used total view

                        // save notification data
                        NotificationType type = notificationTypeRepo.getDetail(10); // view card

                        Notification notification = new Notification();

                        notification.setUserData(userLogin);
                        notification.setTargetUserData(card.getUserData());
                        notification.setTargetUserCard(card);
                        notification.setNotificationType(type);
                        notification.setIsRead(0);
                        notification.setCreatedDate(new Date());
                        notification.setIsActive(1);

                        notificationRepo.save(notification);
                        // end of save notification data
                    } else if (userLogin == null) {
                        // user login is null or anonymous user
                        // set used total view
                        user.setUsedTotalView(user.getUsedTotalView() + 1);
                        user.setUpdatedDate(new Date());

                        userDataRepo.save(user);
                        // end of set used total view

                        // save notification data
                        NotificationType type = notificationTypeRepo.getDetail(10); // view card

                        Notification notification = new Notification();

                        notification.setUserData(null);
                        notification.setTargetUserData(card.getUserData());
                        notification.setTargetUserCard(card);
                        notification.setNotificationType(type);
                        notification.setIsRead(0);
                        notification.setCreatedDate(new Date());
                        notification.setIsActive(1);

                        notificationRepo.save(notification);
                        // end of save notification data
                    }
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity lockCard(Long id, int type) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                LOGGER.error("Only Pro Users that can Lock Their Cards");
                throw new BadRequestException("Only Pro Users that can Lock Their Cards");
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity checkCreateCard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserProfile profile = userProfileRepo.getDetailByUserId(userLogin.getId());
            if (profile.getIsEmailVerified() == 0) {
                LOGGER.error("Your Email has not been Verified");
                throw new BadRequestException("Your Email has not been Verified");
            }

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
                if (countCard == 10) {
                    LOGGER.error("You have reached your card limit. User Pro can only have 10 cards");
                    throw new BadRequestException("You have reached your card limit. User Pro can only have 10 cards");
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
    public ResponseEntity getDetail(Long id, int typeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getMaxTotalView() == userLogin.getUsedTotalView()) {
                LOGGER.error("Your Total Action & View is running out. Please purchase more to continue.");
                throw new UnprocessableEntityException("Your Total Action & View is running out. Please purchase more to continue.");
            }

            // 1 = profile pic
            // 2 = mobile number
            // 3 = sms
            // 4 = whatsapp
            // 5 = email
            // 6 = exchange card
            // 7 = view detail card
            // 8 = download contact
            // 9 = request to view card
            // 10 = view card
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                // set used total view
                UserData user = card.getUserData();
                if (!userLogin.getId().equals(user.getId())) {
                    user.setUsedTotalView(user.getUsedTotalView() + 1);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    // save notification data
                    NotificationType type = notificationTypeRepo.getDetail(typeId);

                    Notification notification = new Notification();

                    notification.setUserData(userLogin);
                    notification.setTargetUserData(card.getUserData());
                    notification.setTargetUserCard(card);
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);

                    notificationRepo.save(notification);
                    // end of save notification data
                }
                // end of set used total view

                // save data card viewers
                if (typeId == 7) {
                    cardViewersService.saveData(card, userLogin);
                }
                // emd of save data card viewers

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadContact(Long id, HttpServletResponse httpResponse) throws IOException {
        BufferedWriter writer = null;
        FileInputStream fis = null;

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getMaxTotalView() == userLogin.getUsedTotalView()) {
                LOGGER.error("Your Total Action & View is running out. Please purchase more to continue.");
                throw new UnprocessableEntityException("Your Total Action & View is running out. Please purchase more to continue.");
            }

            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                // set used total view
                UserData user = card.getUserData();
                if (!userLogin.getId().equals(user.getId())) {
                    user.setUsedTotalView(user.getUsedTotalView() + 1);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    NotificationType type = notificationTypeRepo.getDetail(8);
                    Notification notification = new Notification();

                    notification.setUserData(userLogin);
                    notification.setTargetUserData(card.getUserData());
                    notification.setTargetUserCard(card);
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);

                    notificationRepo.save(notification);
                }
                // end of set used total view
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }

            String dialCodeWa = "";
            CardPhoneDetail phoneWa = cardPhoneDetailRepo.getDetail(card.getId(), 1);
            if (phoneWa != null) {
                dialCodeWa = phoneWa.getDialCode();
            }

            String dialCodeMobile = "";
            CardPhoneDetail phoneMobile = cardPhoneDetailRepo.getDetail(card.getId(), 2);
            if (phoneMobile != null) {
                dialCodeMobile = phoneMobile.getDialCode();
            }

            String filename = new Date().getTime() + "_" + UUID.randomUUID().toString() + ".vcf";
            File contactFolder = new File(contactDir);
            if (!contactFolder.exists()) {
                contactFolder.mkdirs();
            }

            String firstName = card.getFirstName();
            String lastName = card.getLastName();
            if (card.getCardType().getId() == 2) {
                // company card
                firstName = card.getCompany();
                lastName = "";
            }

            String data = "BEGIN:VCARD\n" +
                    "VERSION:2.1\n" +
                    "N:" + lastName + ";" + firstName + ";;\n" +
                    "FN:"+ firstName + " " + lastName + "\n" +
                    "ORG:"+ card.getCompany() + "\n" +
                    "TITLE:\n" +
                    "PHOTO;GIF:http://www.example.com/dir_photos/my_photo.gif\n" +
                    "TEL;WORK;VOICE:" + dialCodeWa + card.getWhatsappNo() + "\n" +
                    "TEL;HOME;VOICE:" + dialCodeMobile + card.getMobileNo() + "\n" +
                    "ADR;HOME:;;" + card.getAddress1() + ";" + card.getAddress2()+ ";" + card.getCity() + ";" + card.getPostalCode() + ";" + card.getCountry() + "\n" +
                    "EMAIL:" + card.getEmail() + "\n" +
                    "REV:20080424T195243Z\n" +
                    "END:VCARD";

            writer = new BufferedWriter(new FileWriter(contactFolder.getAbsoluteFile() + File.separator + filename));
            writer.write(data);

            if (writer != null) {
                writer.close();

                httpResponse.setContentType("text/vcard");
                httpResponse.setHeader("Content-Disposition", "attachment; filename=" + filename);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                fis = new FileInputStream(new File(contactFolder + File.separator + filename));

                return IOUtils.toByteArray(fis);
            } else {
                LOGGER.error("Error Generate File Contact Card");
                throw new InternalServerErrorException("Error Generate File Contact Card");
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                // end of insert to table request to view card

                // set used total view
                UserData user = card.getUserData();
                if (!userLogin.getId().equals(user.getId())) {
                    user.setUsedTotalView(user.getUsedTotalView() + 1);
                    user.setUpdatedDate(new Date());

                    userDataRepo.save(user);

                    // insert to table notification
                    NotificationType type = notificationTypeRepo.getDetail(9);
                    Notification notification = new Notification();

                    notification.setUserData(userLogin);
                    notification.setTargetUserData(card.getUserData());
                    notification.setTargetUserCard(card);
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);
                    notification.setParamId(request.getId());

                    notificationRepo.save(notification);
                    // end of insert to table notification
                }
                // end of set used total view

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
                    request.setIsGranted(2);
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailLockedCard(String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if (userLogin.getId().equals(card.getUserData().getId())) {
                    // card owner
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            generateRes.generateResponseUserCard(card)), HttpStatus.OK);
                }

                CardRequestView request = cardRequestViewRepo.getDetail(card.getId(), userLogin.getId());
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
                LOGGER.error("User Card Code: " + code + " is not found");
                throw new NotFoundException("User Card Code: " + code);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailCheckSession(Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                if (card.getUserData().getId().equals(userSession.getId())) {
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            generateRes.generateResponseUserCard(card)), HttpStatus.OK);
                } else {
                    LOGGER.error("This Card is not Yours");
                    throw new UnprocessableEntityException("This Card is not Yours");
                }
            } else {
                LOGGER.error("User Card ID: " + id + " is not found");
                throw new NotFoundException("User Card ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetailCheckSession(String code) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();

            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if (card.getUserData().getId().equals(userSession.getId())) {
                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            generateRes.generateResponseUserCard(card)), HttpStatus.OK);
                } else {
                    LOGGER.error("This Card is not Yours");
                    throw new UnprocessableEntityException("This Card is not Yours");
                }
            } else {
                LOGGER.error("User Card Code: " + code + " is not found");
                throw new NotFoundException("User Card Code: " + code);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity exchangeCard(Long fromCardId, Long exchangeCardId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getMaxTotalView() == userLogin.getUsedTotalView()) {
                LOGGER.error("Your Total Action & View is running out. Please purchase more to continue.");
                throw new UnprocessableEntityException("Your Total Action & View is running out. Please purchase more to continue.");
            }

            UserCard fromCard = userCardRepo.getDetail(fromCardId);
            if (fromCard == null) {
                LOGGER.error("From User Card ID: " + fromCardId + " is not found");
                throw new NotFoundException("From User Card ID: " + fromCardId);
            }

            // now it's possibly to exchange without exchange card
            UserCard exchangeCard = userCardRepo.getDetail(exchangeCardId);
//            if (exchangeCard == null) {
//                LOGGER.error("Exchange User Card ID: " + exchangeCardId + " is not found");
//                throw new NotFoundException("Exchange User Card ID: " + exchangeCardId);
//            }

            // save request exchange card
            UserContact userContact = null;
            Page<UserContact> pageContact = userContactRepo.getDetail(
                    userLogin.getId(),
                    fromCardId,
                    exchangeCardId,
                    PageRequest.of(0, 1, Sort.by("id").descending()));

            if (pageContact.getContent() != null && pageContact.getContent().size() > 0) {
                UserContact existingContact = pageContact.getContent().get(0);
                if (existingContact.getStatus() == 2 || existingContact.getStatus() == 3) {
                    // rejected or canceled
                    existingContact.setStatus(0);
                    existingContact.setUpdatedDate(new Date());

                    userContactRepo.save(existingContact);

                    userContact = existingContact;
                } else if (existingContact.getStatus() == 0) {
                    LOGGER.error("You Already Requested for Exchange Card");
                    throw new UnprocessableEntityException("You Already Requested for Exchange Card");
                } else {
                    LOGGER.error("Your Exchange Card Request Already Accepted");
                    throw new UnprocessableEntityException("Your Exchange Card Request Already Accepted");
                }
            } else {
                UserContact contact = new UserContact();

                contact.setUserData(userLogin);
                contact.setFromCard(fromCard);
                contact.setExchangeCard(exchangeCard);
                contact.setStatus(0); // requested
                contact.setFlag(1); // flag request exchange
                contact.setCreatedDate(new Date());

                userContactRepo.save(contact);

                userContact = contact;
            }
            // end of save request exchange card

            // set used total view
            UserData user = exchangeCard.getUserData();
            if (!userLogin.getId().equals(user.getId())) {
                user.setUsedTotalView(user.getUsedTotalView() + 1);
                user.setUpdatedDate(new Date());

                userDataRepo.save(user);

                // save notification data
                NotificationType type = notificationTypeRepo.getDetail(6); // exchange card

                Notification notification = new Notification();

                notification.setUserData(userLogin);
                notification.setTargetUserData(exchangeCard.getUserData());
                notification.setTargetUserCard(exchangeCard);
                notification.setNotificationType(type);
                notification.setIsRead(0);
                notification.setCreatedDate(new Date());
                notification.setIsActive(1);

                if (userContact != null) {
                    notification.setParamId(userContact.getId());
                }

                notificationRepo.save(notification);
                // end of save notification data
            }
            // end of set used total view

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
    public ResponseEntity updateAllCode() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (userLogin.getRoleData().getId() != 1) {
                LOGGER.error("Administrator Only");
                throw new BadRequestException("Administrator Only");
            }

            List<UserCard> cards = userCardRepo.getList();
            if (cards != null) {
                for (UserCard item : cards) {
                    item.setUniqueCode(StringUtil.generateUniqueCodeCard());
                    item.setUpdatedDate(new Date());

                    userCardRepo.save(item);
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
    public ResponseEntity sendNotifyUpdateCard(Long id, List<Long> userIds) {
        try {
            UserCard card = userCardRepo.getDetail(id);
            if (card != null) {
                // notify updated card to contact
                new Thread(() -> {
                    cardViewersService.notifyUpdatedCard(card, userIds);
                }).start();
                // end of notify updated card to contact

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
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
