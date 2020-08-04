package com.lynknow.api.util;

import com.lynknow.api.model.*;
import com.lynknow.api.pojo.response.*;
import com.lynknow.api.repository.CardPhoneDetailRepository;
import com.lynknow.api.repository.UserPhoneDetailRepository;
import com.lynknow.api.repository.UserProfileRepository;
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

    public RoleDataResponse generateResponseRole(RoleData role) {
        RoleDataResponse res = new RoleDataResponse();

        if (role == null) {
            return res;
        }

        res.setId(role.getId());
        res.setName(role.getName());
        res.setDescription(role.getDescription());

        return res;
    }

    public SubscriptionPackageResponse generateResponseSubscription(SubscriptionPackage subs) {
        SubscriptionPackageResponse res = new SubscriptionPackageResponse();

        if (subs == null) {
            return res;
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
            return res;
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
            resDetail.setNumber(detail.getNumber());

            res.setWhatsappNo(resDetail);
        }

        Page<UserPhoneDetail> pageMobile = userPhoneDetailRepo.getDetail(profile.getId(), 2, PageRequest.of(0, 1, Sort.by("id").descending()));
        if (pageMobile.getContent() != null && pageMobile.getContent().size() > 0) {
            UserPhoneDetail detail = pageMobile.getContent().get(0);

            PhoneDetailResponse resDetail = new PhoneDetailResponse();

            resDetail.setId(detail.getId());
            resDetail.setCountryCode(detail.getCountryCode());
            resDetail.setDialCode(detail.getDialCode());
            resDetail.setNumber(detail.getNumber());

            res.setMobileNo(resDetail);
        }

        return res;
    }

    public UserProfilePublicResponse generateResponseProfilePublic(UserProfile profile) {
        UserProfilePublicResponse res = new UserProfilePublicResponse();

        if (profile == null) {
            return res;
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
            return res;
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
            res.setProfile(generateResponseProfile(profile));
        }

        return res;
    }

    public UserDataPublicResponse generateResponseUserPublic(UserData user) {
        UserDataPublicResponse res = new UserDataPublicResponse();

        if (user == null) {
            return res;
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

        if (type == null) {
            return res;
        }

        res.setId(type.getId());
        res.setName(type.getName());
        res.setDescription(type.getDescription());

        return res;
    }

    public UserCardResponse generateResponseUserCard(UserCard card) {
        UserCardResponse res = new UserCardResponse();

        if (card == null) {
            return res;
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
            return res;
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
        res.setCreatedDate(card.getCreatedDate());
        res.setUpdatedDate(card.getUpdatedDate());

        return res;
    }

    public CountryResponse generateResponseCountry(Country country) {
        CountryResponse res = new CountryResponse();

        if (country == null) {
            return res;
        }

        res.setId(country.getId());
        res.setIso(country.getIso());
        res.setName(country.getName());
        res.setNiceName(country.getNiceName());
        res.setIso3(country.getIso3());
        res.setNumCode(country.getNumCode());
        res.setPhoneCode(country.getPhoneCode());

        return res;
    }

}
