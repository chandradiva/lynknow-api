package com.lynknow.api.repository;

import com.lynknow.api.model.CardPhoneDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardPhoneDetailRepository extends JpaRepository<CardPhoneDetail, Long> {

    @Query("FROM CardPhoneDetail WHERE id = :id")
    CardPhoneDetail getDetail(@Param("id") Long id);

    @Query("FROM CardPhoneDetail WHERE userCard.id = :cardId AND type = :type")
    CardPhoneDetail getDetail(@Param("cardId") Long cardId, @Param("type") int type);

    @Query("SELECT det FROM CardPhoneDetail det " +
            "WHERE det.userCard.id = :cardId " +
            "AND det.type = :type")
    Page<CardPhoneDetail> getDetail(
            @Param("cardId") Long cardId,
            @Param("type") int type,
            Pageable pageable);

}
