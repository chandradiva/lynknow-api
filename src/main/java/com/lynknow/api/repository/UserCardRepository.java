package com.lynknow.api.repository;

import com.lynknow.api.model.UserCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    @Query("FROM UserCard WHERE isActive = 1 AND id = :id")
    UserCard getDetail(@Param("id") Long id);

    @Query("SELECT card FROM UserCard card " +
            "WHERE card.isActive = 1 " +
            "AND (:userId IS NULL OR card.userData.id = :userId) " +
            "AND (:typeId IS NULL OR card.cardType.id = :typeId) " +
            "AND (:isPublished IS NULL OR card.isPublished = :isPublished)")
    List<UserCard> getList(
            @Param("userId") Long userId,
            @Param("typeId") Integer typeId,
            @Param("isPublished") Integer isPublished,
            Sort sort);

    @Query("SELECT card FROM UserCard card " +
            "WHERE card.isActive = 1 " +
            "AND (:userId IS NULL OR card.userData.id = :userId) " +
            "AND (:typeId IS NULL OR card.cardType.id = :typeId) " +
            "AND (:isPublished IS NULL OR card.isPublished = :isPublished) " +
            "AND (LOWER(card.company) LIKE %:param% " +
            "OR LOWER(card.address1) LIKE %:param% " +
            "OR LOWER(card.address2) LIKE %:param%)")
    Page<UserCard> getListPagination(
            @Param("userId") Long userId,
            @Param("typeId") Integer typeId,
            @Param("isPublished") Integer isPublished,
            @Param("param") String param,
            Pageable pageable);

    @Query("FROM UserCard WHERE isActive = 1 AND uniqueCode = :code")
    UserCard getByUniqueCode(@Param("code") String code);

}
