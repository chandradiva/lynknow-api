package com.lynknow.api.util;

import com.lynknow.api.model.*;
import com.lynknow.api.pojo.response.*;
import com.lynknow.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class GenerateResponseUtil {

    @Autowired
    private UserProfileRepository userProfileRepo;

    @Autowired
    private UserPhoneDetailRepository userPhoneDetailRepo;

    @Autowired
    private CardPhoneDetailRepository cardPhoneDetailRepo;

    @Autowired
    private CardRequestViewRepository cardRequestViewRepo;

    public RoleDataResponse generateResponseRole(RoleData role) {
        RoleDataResponse res = new RoleDataResponse();

        res.setId(role.getId());
        res.setName(role.getName());
        res.setDescription(role.getDescription());

        return res;
    }

    public SubscriptionPackageResponse generateResponseSubscription(SubscriptionPackage subs) {
        SubscriptionPackageResponse res = new SubscriptionPackageResponse();

        if (subs == null) {
            return null;
        }

        res.setId(subs.getId());
        res.setName(subs.getName());
        res.setDescription(subs.getDescription());
        res.setPrice(subs.getPrice());
        res.setRemarks(subs.getRemarks());
        res.setCreatedDate(subs.getCreatedDate());
        res.setUpdatedDate(subs.getUpdatedDate());

        return res;
    }

    public UserProfileResponse generateResponseProfile(UserProfile profile) {
        UserProfileResponse res = new UserProfileResponse();

        if (profile == null) {
            return null;
        }

        res.setId(profile.getId());
        res.setFirstName(profile.getFirstName());
        res.setLastName(profile.getLastName());
        res.setAddress1(profile.getAddress1());
        res.setAddress2(profile.getAddress2());
        res.setCountry(profile.getCountry());
        res.setFbId(profile.getFbId());
        res.setFbToken(profile.getFbToken());
        res.setFbEmail(profile.getFbEmail());
        res.setGoogleId(profile.getGoogleId());
        res.setGoogleToken(profile.getGoogleToken());
        res.setGoogleEmail(profile.getGoogleEmail());
        res.setIsWhatsappNoVerified(profile.getIsWhatsappNoVerified());
        res.setIsEmailVerified(profile.getIsEmailVerified());
        res.setProfilePhoto(profile.getProfilePhoto());
        res.setCreatedDate(profile.getCreatedDate());
        res.setUpdatedDate(profile.getUpdatedDate());

        Page<UserPhoneDetail> pageWa = userPhoneDetailRepo.getDetail(profile.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
            UserPhoneDetail detail = pageWa.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(StringUtil.normalizePhoneNumber(detail.getNumber()));

            res.setWhatsappNo(resDetail);
        }

        Page<UserPhoneDetail> pageMobile = userPhoneDetailRepo.getDetail(profile.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
            UserPhoneDetail detail = pageMobile.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(StringUtil.normalizePhoneNumber(detail.getNumber()));

            res.setMobileNo(resDetail);
        }

        return res;
    }

    public UserProfilePublicResponse generateResponseProfilePublic(UserProfile profile) {
        UserProfilePublicResponse res = new UserProfilePublicResponse();

        if (profile == null) {
            return null;
        }

        res.setId(profile.getId());
        res.setFirstName(profile.getFirstName());
        res.setLastName(profile.getLastName());
        res.setAddress1(profile.getAddress1());
        res.setAddress2(profile.getAddress2());
        res.setCountry(profile.getCountry());
        res.setCreatedDate(profile.getCreatedDate());
        res.setUpdatedDate(profile.getUpdatedDate());

        return res;
    }

    public UserDataResponse generateResponseUser(UserData user) {
        UserDataResponse res = new UserDataResponse();

        if (user == null) {
            return null;
        }

        res.setId(user.getId());
        res.setRole(generateResponseRole(user.getRoleData()));
        res.setCurrentSubscription(generateResponseSubscription(user.getCurrentSubscriptionPackage()));
        res.setProfile(null);
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setVerificationPoint(user.getVerificationPoint());
        res.setJoinDate(user.getJoinDate());
        res.setCreatedDate(user.getCreatedDate());
        res.setUpdatedDate(user.getUpdatedDate());
        res.setMaxVerificationCredit(user.getMaxVerificationCredit());
        res.setCurrentVerificationCredit(user.getCurrentVerificationCredit());

        return res;
    }

    public UserDataResponse generateResponseUserComplete(UserData user) {
        UserDataResponse res = new UserDataResponse();

        if (user == null) {
            return null;
        }

        res.setId(user.getId());
        res.setRole(generateResponseRole(user.getRoleData()));
        res.setCurrentSubscription(generateResponseSubscription(user.getCurrentSubscriptionPackage()));
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setVerificationPoint(user.getVerificationPoint());
        res.setJoinDate(user.getJoinDate());
        res.setCreatedDate(user.getCreatedDate());
        res.setUpdatedDate(user.getUpdatedDate());
        res.setMaxVerificationCredit(user.getMaxVerificationCredit());
        res.setCurrentVerificationCredit(user.getCurrentVerificationCredit());

        UserProfile profile = userProfileRepo.getDetailByUserId(user.getId());
        if (profile != null) {
            res.setProfile(generateResponseProfile(profile));
        }

        return res;
    }

    public UserDataPublicResponse generateResponseUserPublic(UserData user) {
        UserDataPublicResponse res = new UserDataPublicResponse();

        if (user == null) {
            return null;
        }

        res.setId(user.getId());
        res.setRole(generateResponseRole(user.getRoleData()));
        res.setCurrentSubscription(generateResponseSubscription(user.getCurrentSubscriptionPackage()));
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setVerificationPoint(user.getVerificationPoint());
        res.setJoinDate(user.getJoinDate());
        res.setCreatedDate(user.getCreatedDate());
        res.setUpdatedDate(user.getUpdatedDate());

        UserProfile profile = userProfileRepo.getDetailByUserId(user.getId());
        if (profile != null) {
            res.setProfile(generateResponseProfilePublic(profile));
        }

        return res;
    }

    public CardTypeResponse generateResponseCardType(CardType type) {
        CardTypeResponse res = new CardTypeResponse();

        res.setId(type.getId());
        res.setName(type.getName());
        res.setDescription(type.getDescription());

        return res;
    }

    public UserCardResponse generateResponseUserCard(UserCard card) {
        UserCardResponse res = new UserCardResponse();

        if (card == null) {
            return null;
        }

        res.setId(card.getId());
        res.setUserData(generateResponseUser(card.getUserData()));
        res.setCardType(generateResponseCardType(card.getCardType()));
        res.setFrontSide(card.getFrontSide());
        res.setBackSide(card.getBackSide());
        res.setProfilePhoto(card.getProfilePhoto());
        res.setFirstName(card.getFirstName());
        res.setLastName(card.getLastName());
        res.setDesignation(card.getDesignation());
        res.setCompany(card.getCompany());
        res.setAddress1(card.getAddress1());
        res.setAddress2(card.getAddress2());
        res.setCity(card.getCity());
        res.setPostalCode(card.getPostalCode());
        res.setCountry(card.getCountry());
        res.setEmail(card.getEmail());
        res.setWebsite(card.getWebsite());
        res.setFbEmail(card.getFbEmail());
        res.setGoogleEmail(card.getGoogleEmail());
        res.setIsPublished(card.getIsPublished());
        res.setPublishedDate(card.getPublishedDate());
        res.setUniqueCode(card.getUniqueCode());
        res.setVerificationPoint(card.getVerificationPoint());
        res.setIsCardLocked(card.getIsCardLocked());
        res.setIsWhatsappNoVerified(card.getIsWhatsappNoVerified());
        res.setIsEmailVerified(card.getIsEmailVerified());
        res.setCreatedDate(card.getCreatedDate());
        res.setUpdatedDate(card.getUpdatedDate());

        Page<CardPhoneDetail> pageWa = cardPhoneDetailRepo.getDetail(card.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
            CardPhoneDetail detail = pageWa.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(detail.getNumber());

            res.setWhatsappNo(resDetail);
        }

        Page<CardPhoneDetail> pageMobile = cardPhoneDetailRepo.getDetail(card.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
            CardPhoneDetail detail = pageMobile.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(detail.getNumber());

            res.setMobileNo(resDetail);
        }

        return res;
    }

    public UserCardResponse generateResponseUserCardWithoutUser(UserCard card) {
        UserCardResponse res = new UserCardResponse();

        if (card == null) {
            return null;
        }

        res.setId(card.getId());
        res.setUserData(null);
        res.setCardType(generateResponseCardType(card.getCardType()));
        res.setFrontSide(card.getFrontSide());
        res.setBackSide(card.getBackSide());
        res.setProfilePhoto(card.getProfilePhoto());
        res.setFirstName(card.getFirstName());
        res.setLastName(card.getLastName());
        res.setDesignation(card.getDesignation());
        res.setCompany(card.getCompany());
        res.setAddress1(card.getAddress1());
        res.setAddress2(card.getAddress2());
        res.setCity(card.getCity());
        res.setPostalCode(card.getPostalCode());
        res.setCountry(card.getCountry());
        res.setEmail(card.getEmail());
        res.setWebsite(card.getWebsite());
        res.setFbEmail(card.getFbEmail());
        res.setGoogleEmail(card.getGoogleEmail());
        res.setIsPublished(card.getIsPublished());
        res.setPublishedDate(card.getPublishedDate());
        res.setUniqueCode(card.getUniqueCode());
        res.setVerificationPoint(card.getVerificationPoint());
        res.setIsCardLocked(card.getIsCardLocked());
        res.setIsWhatsappNoVerified(card.getIsWhatsappNoVerified());
        res.setIsEmailVerified(card.getIsEmailVerified());
        res.setCreatedDate(card.getCreatedDate());
        res.setUpdatedDate(card.getUpdatedDate());

        Page<CardPhoneDetail> pageWa = cardPhoneDetailRepo.getDetail(card.getId(), 1, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageWa.getContent() != null && pageWa.getContent().size() > 0) {
            CardPhoneDetail detail = pageWa.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(detail.getNumber());

            res.setWhatsappNo(resDetail);
        }

        Page<CardPhoneDetail> pageMobile = cardPhoneDetailRepo.getDetail(card.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
            CardPhoneDetail detail = pageMobile.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(detail.getNumber());

            res.setMobileNo(resDetail);
        }

        return res;
    }

    public UserCardPublicResponse generateResponseUserCardPublic(UserCard card) {
        UserCardPublicResponse res = new UserCardPublicResponse();

        if (card == null) {
            return null;
        }

        res.setId(card.getId());
        res.setUserData(generateResponseUserPublic(card.getUserData()));
        res.setCardType(generateResponseCardType(card.getCardType()));
        res.setFrontSide(card.getFrontSide());
        res.setBackSide(card.getBackSide());
        res.setProfilePhoto(card.getProfilePhoto());
        res.setIsPublished(card.getIsPublished());
        res.setPublishedDate(card.getPublishedDate());
        res.setUniqueCode(card.getUniqueCode());
        res.setVerificationPoint(card.getVerificationPoint());
        res.setIsCardLocked(card.getIsCardLocked());
        res.setCreatedDate(card.getCreatedDate());
        res.setUpdatedDate(card.getUpdatedDate());

        return res;
    }

    public CountryResponse generateResponseCountry(Country country) {
        CountryResponse res = new CountryResponse();

        res.setId(country.getId());
        res.setIso(country.getIso());
        res.setName(country.getName());
        res.setNiceName(country.getNiceName());
        res.setIso3(country.getIso3());
        res.setNumCode(country.getNumCode());
        res.setPhoneCode(country.getPhoneCode());

        return res;
    }

    public OtpTypeResponse generateResponseOtpType(OtpType type) {
        OtpTypeResponse res = new OtpTypeResponse();

        res.setId(type.getId());
        res.setName(type.getName());

        return res;
    }

    public NotificationTypeResponse generateResponseNotificationType(NotificationType type) {
        NotificationTypeResponse res = new NotificationTypeResponse();

        res.setId(type.getId());
        res.setName(type.getName());

        return res;
    }

    public NotificationResponse generateResponseNotification(Notification notification) {
        NotificationResponse res = new NotificationResponse();

        res.setId(notification.getId());
        res.setUserData(generateResponseUser(notification.getUserData()));
        res.setTargetUserData(generateResponseUser(notification.getTargetUserData()));
        res.setTargetUserCard(generateResponseUserCard(notification.getTargetUserCard()));
        res.setNotificationType(generateResponseNotificationType(notification.getNotificationType()));
        res.setRemarks(notification.getRemarks());
        res.setIsRead(notification.getIsRead());
        res.setCreatedDate(notification.getCreatedDate());
        res.setUpdatedDate(notification.getUpdatedDate());
        res.setParamId(notification.getParamId());

        if (notification.getNotificationType().getId() == 9) {
            // request to view card
            CardRequestView requestView = cardRequestViewRepo.getDetail(notification.getParamId());
            if (requestView != null) {
                CardRequestViewResponse requestViewRes = generateResponseCardRequestView(requestView);
                res.setAdditionalData(requestViewRes);
            }
        }

        return res;
    }

    public CardVerificationItemResponse generateResponseCardVerificationItem(CardVerificationItem item) {
        CardVerificationItemResponse res = new CardVerificationItemResponse();

        res.setId(item.getId());
        res.setName(item.getName());
        res.setDescription(item.getDescription());
        res.setType(item.getType());

        return res;
    }

    public CardVerificationResponse generateResponseCardVerification(CardVerification verification) {
        CardVerificationResponse res = new CardVerificationResponse();

        res.setId(verification.getId());
        res.setUserCard(generateResponseUserCard(verification.getUserCard()));
        res.setCardVerificationItem(generateResponseCardVerificationItem(verification.getCardVerificationItem()));
        res.setIsVerified(verification.getIsVerified());
        res.setParam(verification.getParam());
        res.setReason(verification.getReason());
        res.setIsRequested(verification.getIsRequested());
        res.setCreatedDate(verification.getCreatedDate());
        res.setUpdatedDate(verification.getUpdatedDate());
        res.setExpiredDate(verification.getExpiredDate());
        res.setVerifiedBy(generateResponseUser(verification.getVerifiedBy()));
        res.setVerifiedDate(verification.getVerifiedDate());

        return res;
    }

    public CardRequestViewResponse generateResponseCardRequestView(CardRequestView request) {
        CardRequestViewResponse res = new CardRequestViewResponse();

        res.setId(request.getId());
        res.setUserCard(generateResponseUserCard(request.getUserCard()));
        res.setUserData(generateResponseUser(request.getUserData()));
        res.setIsGranted(request.getIsGranted());
        res.setExpiredRequestDate(request.getExpiredRequestDate());
        res.setCreatedDate(request.getCreatedDate());
        res.setUpdatedDate(request.getUpdatedDate());
        res.setIsActive(request.getIsActive());

        return res;
    }

    public UserContactResponse generateResponseUserContact(UserContact contact) {
        UserContactResponse res = new UserContactResponse();

        if (contact == null) {
            return null;
        }

        res.setId(contact.getId());
        res.setUserData(generateResponseUser(contact.getUserData()));
        res.setFromCard(generateResponseUserCardWithoutUser(contact.getFromCard()));
        res.setExchangeCard(generateResponseUserCardWithoutUser(contact.getExchangeCard()));
        res.setStatus(contact.getStatus());
        res.setFlag(contact.getFlag());
        res.setCreatedDate(contact.getCreatedDate());
        res.setUpdatedDate(contact.getUpdatedDate());

        return res;
    }

    public PersonalVerificationItemResponse generateResponsePersonalVerificationItem(PersonalVerificationItem item) {
        PersonalVerificationItemResponse res = new PersonalVerificationItemResponse();

        if (item == null) {
            return null;
        }

        res.setId(item.getId());
        res.setName(item.getName());
        res.setDescription(item.getDescription());
        res.setType(item.getType());

        return res;
    }

    public PersonalVerificationResponse generateResponsePersonalVerification(PersonalVerification verification) {
        PersonalVerificationResponse res = new PersonalVerificationResponse();

        if (verification == null) {
            return null;
        }

        res.setId(verification.getId());
        res.setUserData(generateResponseUser(verification.getUserData()));
        res.setPersonalVerificationItem(generateResponsePersonalVerificationItem(verification.getPersonalVerificationItem()));
        res.setRemarks(verification.getRemarks());
        res.setParam(verification.getParam());
        res.setIsVerified(verification.getIsVerified());
        res.setIsRequested(verification.getIsRequested());
        res.setExpiredDate(verification.getExpiredDate());
        res.setVerifiedBy(generateResponseUser(verification.getVerifiedBy()));
        res.setVerifiedDate(verification.getVerifiedDate());
        res.setReason(verification.getReason());
        res.setCreatedDate(verification.getCreatedDate());
        res.setUpdatedDate(verification.getUpdatedDate());

        return res;
    }

}
