package com.lynknow.api.repository;

import com.lynknow.api.model.CardRequestView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRequestViewRepository extends JpaRepository<CardRequestView, Long> {

    @Query("FROM CardRequestView WHERE id = :id AND isActive = 1")
    CardRequestView getDetail(@Param("id") Long id);

    @Query("FROM CardRequestView WHERE userCard.id = :cardId AND userData.id = :userId AND isActive = 1")
    CardRequestView getDetail(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Query("FROM CardRequestView WHERE userData.id = :userId AND isActive = 1 ORDER BY id ASC")
    List<CardRequestView> getList(@Param("userId") Long userId);

}
