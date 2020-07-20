package com.lynknow.api.util;

import com.lynknow.api.model.RoleData;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.response.RoleDataResponse;
import com.lynknow.api.pojo.response.SubscriptionPackageResponse;
import com.lynknow.api.pojo.response.UserDataResponse;

import java.util.function.Function;

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

    public static UserDataResponse generateResponseUser(UserData user) {
        UserDataResponse res = new UserDataResponse();

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

        return res;
    }

}
