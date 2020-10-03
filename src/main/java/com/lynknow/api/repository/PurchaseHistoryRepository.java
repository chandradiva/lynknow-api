package com.lynknow.api.repository;

import com.lynknow.api.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    @Query("FROM PurchaseHistory WHERE id = :id")
    PurchaseHistory getDetail(@Param("id") Long id);

}
