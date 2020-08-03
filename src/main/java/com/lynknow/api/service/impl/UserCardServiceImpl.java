package com.lynknow.api.service.impl;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.CardType;
import com.lynknow.api.model.UserCard;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.UserCardRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserCardResponse;
import com.lynknow.api.repository.CardTypeRepository;
import com.lynknow.api.repository.UserCardRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.repository.UserProfileRepository;
import com.lynknow.api.service.UserCardService;
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

    @Value("${upload.dir.card.front-side}")
    private String frontSideDir;

    @Value("${upload.dir.card.back-side}")
    private String backSideDir;

    @Value("${upload.dir.card.profile-pic}")
    private String profilePicDir;

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
                card.setWhatsappNo(request.getWhatsappNo());
                card.setMobileNo(request.getMobileNo());
                card.setIsPublished(0);
                card.setUniqueCode(UUID.randomUUID().toString());
                card.setCreatedDate(new Date());
                card.setIsActive(1);

                userCardRepo.save(card);

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
                    card.setWhatsappNo(request.getWhatsappNo());
                    card.setMobileNo(request.getMobileNo());
                    card.setUpdatedDate(new Date());

                    userCardRepo.save(card);

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
//                    File fileUpload = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
//                    if (fileUpload.exists()) {
//                        fileUpload.delete();
//                    }

                    LOGGER.error("User Card ID: " + id + " is not found");
//                    throw new NotFoundException("User Card ID: " + id);
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
//                    File fileUpload = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
//                    if (fileUpload.exists()) {
//                        fileUpload.delete();
//                    }

                    LOGGER.error("User Card ID: " + id + " is not found");
//                    throw new NotFoundException("User Card ID: " + id);
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
//                    File fileUpload = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
//                    if (fileUpload.exists()) {
//                        fileUpload.delete();
//                    }

                    LOGGER.error("User Card ID: " + id + " is not found");
//                    throw new NotFoundException("User Card ID: " + id);
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
            UserCard card = userCardRepo.getByUniqueCode(code);
            if (card != null) {
                if (card.getUserData().getCurrentSubscriptionPackage().getId() == 2
                        && card.getIsCardLocked() == 1) {
                    // card locked
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

}
