package com.lynknow.api.repository;

import com.lynknow.api.model.PersonalVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalVerificationRepository extends JpaRepository<PersonalVerification, Long> {

    @Query("FROM PersonalVerification WHERE id = :id")
    PersonalVerification getDetail(@Param("id") Long id);

    @Query("FROM PersonalVerification " +
            "WHERE userData.id = :userId " +
            "AND personalVerificationItem.id = :itemId")
    PersonalVerification getDetail(@Param("userId") Long userId, @Param("itemId") Integer itemId);

    @Query("FROM PersonalVerification " +
            "WHERE userData.id = :userId " +
            "ORDER BY personalVerificationItem.id ASC")
    List<PersonalVerification> getList(@Param("userId") Long userId);

}
