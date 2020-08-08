package com.lynknow.api.repository;

import com.lynknow.api.model.CardVerificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardVerificationItemRepository extends JpaRepository<CardVerificationItem, Integer> {

    @Query("FROM CardVerificationItem WHERE id = :id")
    CardVerificationItem getDetail(@Param("id") Integer id);

    @Query("FROM CardVerificationItem ORDER BY id ASC")
    List<CardVerificationItem> getList();

}
