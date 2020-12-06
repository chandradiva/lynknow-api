package com.lynknow.api.repository;

import com.lynknow.api.model.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

    @Query("FROM UserData WHERE id = :id AND isActive = 1")
    UserData getDetail(@Param("id") Long id);

    @Query("FROM UserData WHERE username = :username AND isActive = 1")
    UserData getByUsername(@Param("username") String username);

    @Query("FROM UserData WHERE email = :email AND isActive = 1")
    UserData getByEmail(@Param("email") String email);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.username) = :username " +
            "AND ud.isActive = 1")
    Page<UserData> getByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.email) = :email " +
            "AND ud.isActive = 1")
    Page<UserData> getByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.fbId) = :fbId " +
            "AND ud.isActive = 1")
    Page<UserData> getByFbId(@Param("fbId") String fbId, Pageable pageable);

    @Query("SELECT ud " +
            "FROM PersonalVerification pv " +
            "JOIN pv.userData ud " +
            "WHERE pv.isRequested = 1 " +
            "GROUP BY ud.id, ud.firstName, ud.lastName, ud.email")
    Page<UserData> getListNeedVerify(Pageable pageable);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.googleId) = :googleId " +
            "AND ud.isActive = 1")
    Page<UserData> getByGoogleId(@Param("googleId") String googleId, Pageable pageable);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE ud.accessToken = :token " +
            "AND ud.isActive = 1")
    Page<UserData> getByAccessToken(@Param("token") String token, Pageable pageable);

    @Query("FROM UserData WHERE isActive = 1")
    List<UserData> getListAll();

    @Query("FROM UserData " +
            "WHERE (currentSubscriptionPackage.id = 2 " +
            "OR currentSubscriptionPackage.id = 3) " +
            "AND expiredPremiumDate < :today " +
            "AND isActive = 1")
    List<UserData> getExpiredUser(@Param("today") Date today);

    @Query("FROM UserData WHERE expiredTotalView < :today AND isActive = 1")
    List<UserData> getExpiredTotalView(@Param("today") Date today);

}
