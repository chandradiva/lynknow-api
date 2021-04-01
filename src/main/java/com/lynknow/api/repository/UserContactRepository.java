package com.lynknow.api.repository;

import com.lynknow.api.model.UserContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {

    @Query("FROM UserContact WHERE id = :id")
    UserContact getDetail(@Param("id") Long id);

    @Query("FROM UserContact WHERE userData.id = :userId AND exchangeCard.id = :exchangeCardId")
    UserContact getDetail(@Param("userId") Long userId, @Param("exchangeCardId") Long exchangeCardId);

    @Query("FROM UserContact WHERE userData.id = :userId AND status = :status")
    List<UserContact> getList(@Param("userId") Long userId, @Param("status") Integer status);

    @Query("SELECT uc FROM UserContact uc " +
            "WHERE uc.exchangeCard.userData.id = :userId " +
            "AND (uc.status = 1 OR (uc.status = 0 AND uc.flag = 1))")
    Page<UserContact> getListPaginationContact(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("SELECT uc FROM UserContact uc " +
            "WHERE uc.userData.id = :userId " +
            "AND uc.fromCard.id = :fromCardId " +
            "AND uc.exchangeCard.id = :exchangeCardId")
    Page<UserContact> getDetail(
            @Param("userId") Long userId,
            @Param("fromCardId") Long fromCardId,
            @Param("exchangeCardId") Long exchangeCardId,
            Pageable pageable);

    @Query("SELECT uc FROM UserContact uc " +
            "WHERE uc.exchangeCard.userData.id = :receivedUserId " +
            "AND (uc.status = 0 OR uc.flag = 1)")
    Page<UserContact> getListPaginationReceived(
            @Param("receivedUserId") Long userId,
            Pageable pageable);

    @Query("SELECT uc FROM UserContact uc " +
            "WHERE uc.exchangeCard.userData.id = :receivedUserId " +
            "AND uc.status = 0 " +
            "AND uc.flag = 1")
    Page<UserContact> getListPaginationRequested(
            @Param("receivedUserId") Long userId,
            Pageable pageable);

}
