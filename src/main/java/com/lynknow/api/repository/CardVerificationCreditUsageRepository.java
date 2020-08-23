package com.lynknow.api.repository;

import com.lynknow.api.model.CardVerificationCreditUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardVerificationCreditUsageRepository extends JpaRepository<CardVerificationCreditUsage, Long> {

    @Query("FROM CardVerificationCreditUsage WHERE id = :id")
    CardVerificationCreditUsage getDetail(@Param("id") Long id);

}
