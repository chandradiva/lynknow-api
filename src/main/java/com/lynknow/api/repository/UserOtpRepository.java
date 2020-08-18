package com.lynknow.api.repository;

import com.lynknow.api.model.UserOtp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    @Query("FROM UserOtp WHERE id = :id AND isActive = 1")
    UserOtp getDetail(@Param("id") Long id);

    @Query("FROM UserOtp " +
            "WHERE userData.id = :userId " +
            "AND otpType.id = :typeId " +
            "AND otpCode = :code " +
            "AND isActive = 1")
    UserOtp getDetail(@Param("userId") Long userId, @Param("typeId") Integer typeId, @Param("code") String code);

    @Query("SELECT otp FROM UserOtp otp " +
            "WHERE otp.userData.id = :userId " +
            "AND otp.otpType.id = :typeId " +
            "AND otp.otpCode = :code " +
            "AND otp.isActive = 1")
    Page<UserOtp> getDetail(
            @Param("userId") Long userId,
            @Param("typeId") Integer typeId,
            @Param("code") String code,
            Pageable pageable);

    @Query("SELECT otp FROM UserOtp otp " +
            "WHERE otp.userData.id = :userId " +
            "AND otp.otpType.id = :typeId " +
            "AND otp.isActive = 1")
    Page<UserOtp> getDetail(
            @Param("userId") Long userId,
            @Param("typeId") Integer typeId,
            Pageable pageable);

    @Query("FROM UserOtp " +
            "WHERE userCard.id = :cardId " +
            "AND otpType.id = :typeId " +
            "AND otpCode = :code " +
            "AND isActive = 1")
    UserOtp getDetailCardOtp(@Param("cardId") Long cardId, @Param("typeId") Integer typeId, @Param("code") String code);

    @Query("SELECT otp FROM UserOtp otp " +
            "WHERE otp.userCard.id = :cardId " +
            "AND otp.otpType.id = :typeId " +
            "AND otp.otpCode = :code " +
            "AND otp.isActive = 1")
    Page<UserOtp> getDetailCardOtp(
            @Param("cardId") Long cardId,
            @Param("typeId") Integer typeId,
            @Param("code") String code,
            Pageable pageable);

    @Query("SELECT otp FROM UserOtp otp " +
            "WHERE otp.userCard.id = :cardId " +
            "AND otp.otpType.id = :typeId " +
            "AND otp.isActive = 1")
    Page<UserOtp> getDetailCardOtp(
            @Param("cardId") Long cardId,
            @Param("typeId") Integer typeId,
            Pageable pageable);

}
