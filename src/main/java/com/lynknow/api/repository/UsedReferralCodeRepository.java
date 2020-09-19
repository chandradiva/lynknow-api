package com.lynknow.api.repository;

import com.lynknow.api.model.UsedReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsedReferralCodeRepository extends JpaRepository<UsedReferralCode, Long> {

    @Query("FROM UsedReferralCode WHERE id = :id AND isActive = 1")
    UsedReferralCode getDetail(@Param("id") Long id);

    @Query("FROM UsedReferralCode WHERE isActive = 1 AND userData.id = :userId AND referralCode = :code")
    UsedReferralCode getDetail(@Param("userId") Long userId, @Param("code") String code);

    @Query("FROM UsedReferralCode WHERE isActive = 1 AND userData.id = :userId")
    List<UsedReferralCode> getListByUser(@Param("userId") Long userId);

    @Query("FROM UsedReferralCode WHERE isActive = 1 AND referralCode = :code")
    List<UsedReferralCode> getListByCode(String code);

}
