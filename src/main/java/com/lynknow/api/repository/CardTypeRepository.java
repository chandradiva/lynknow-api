package com.lynknow.api.repository;

import com.lynknow.api.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {

    @Query("FROM CardType WHERE id = :id")
    CardType getDetail(@Param("id") Integer id);

    @Query("FROM CardType ORDER BY id ASC")
    List<CardType> getList();

}
