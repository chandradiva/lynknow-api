package com.lynknow.api.util;

import com.lynknow.api.model.*;
import com.lynknow.api.pojo.response.*;

public class GenerateResponseUtil {

    public static RoleDataResponse generateResponseRole(RoleData role) {
        RoleDataResponse res = new RoleDataResponse();

        res.setId(role.getId());
        res.setName(role.getName());
        res.setDescription(role.getDescription());

        return res;
    }

    public static SubscriptionPackageResponse generateResponseSubscription(SubscriptionPackage subs) {
        SubscriptionPackageResponse res = new SubscriptionPackageResponse();

        res.setId(subs.getId());
        res.setName(subs.getName());
        res.setDescription(subs.getDescription());
        res.setPrice(subs.getPrice());
        res.setRemarks(subs.getRemarks());
        res.setCreatedDate(subs.getCreatedDate());
        res.setUpdatedDate(subs.getUpdatedDate());

        return res;
    }

    public static UserProfileResponse generateResponseProfile(UserProfile profile) {
        UserProfileResponse res = new UserProfileResponse();

        return res;
    }

    public static UserDataResponse generateResponseUser(UserData user) {
        UserDataResponse res = new UserDataResponse();

        res.setId(user.getId());
        res.setRole(generateResponseRole(user.getRoleData()));
        res.setCurrentSubscription(generateResponseSubscription(user.getCurrentSubscriptionPackage()));
        res.setProfile(generateResponseProfile(null));
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setVerificationPoint(user.getVerificationPoint());
        res.setJoinDate(user.getJoinDate());
        res.setCreatedDate(user.getCreatedDate());
        res.setUpdatedDate(user.getUpdatedDate());

        return res;
    }

    public static CardTypeResponse generateResponseCardType(CardType type) {
        CardTypeResponse res = new CardTypeResponse();

        res.setId(type.getId());
        res.setName(type.getName());
        res.setDescription(type.getDescription());

        return res;
    }

    public static UserCardResponse generateResponseUserCard(UserCard card) {
        UserCardResponse res = new UserCardResponse();

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
        res.setWhatsappNo(card.getWhatsappNo());
        res.setMobileNo(card.getMobileNo());
        res.setFbEmail(card.getFbEmail());
        res.setGoogleEmail(card.getGoogleEmail());
        res.setIsPublished(card.getIsPublished());
        res.setPublishedDate(card.getPublishedDate());
        res.setUniqueCode(card.getUniqueCode());
        res.setCreatedDate(card.getCreatedDate());
        res.setUpdatedDate(card.getUpdatedDate());

        return res;
    }

}
