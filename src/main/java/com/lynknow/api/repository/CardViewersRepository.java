package com.lynknow.api.repository;

import com.lynknow.api.model.CardViewers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardViewersRepository extends JpaRepository<CardViewers, Long> {

    @Query("FROM CardViewers WHERE id = :id AND isActive = 1")
    CardViewers getDetail(@Param("id") Long id);

    @Query("SELECT cv FROM CardViewers cv " +
            "WHERE (:cardSeenId IS NULL OR cv.cardSeen.id = :cardSeenId) " +
            "AND (:userSeenId IS NULL OR cv.userSeenBy.id = :userSeenId) " +
            "AND cv.isActive = 1")
    Page<CardViewers> getByCardSeen(
            @Param("cardSeenId") Long cardSeenId,
            @Param("userSeenId") Long userSeenId,
            Pageable pageable);

    @Query("SELECT cv FROM CardViewers cv " +
            "WHERE cv.cardSeen.userData.id = :userId " +
            "AND cv.isActive = 1")
    Page<CardViewers> getListViewers(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("FROM CardViewers " +
            "WHERE cardSeen.id = :cardId " +
            "AND userSeenBy.id IN (:userIds) " +
            "AND isActive = 1")
    List<CardViewers> getListViewers(
            @Param("cardId") Long cardId,
            @Param("userIds") List<Long> userIds);

}
