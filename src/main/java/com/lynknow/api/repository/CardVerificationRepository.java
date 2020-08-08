package com.lynknow.api.repository;

import com.lynknow.api.model.CardVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardVerificationRepository extends JpaRepository<CardVerification, Long> {

    @Query("FROM CardVerification WHERE id = :id")
    CardVerification getDetail(@Param("id") Long id);

    @Query("FROM CardVerification WHERE userCard.id = :cardId AND cardVerificationItem.id = :itemId")
    CardVerification getDetail(@Param("cardId") Long cardId, @Param("itemId") Integer itemId);

    @Query("FROM CardVerification WHERE userCard.id = :cardId ORDER BY cardVerificationItem.id ASC")
    List<CardVerification> getList(@Param("cardId") Long cardId);

}
