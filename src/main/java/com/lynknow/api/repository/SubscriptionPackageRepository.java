package com.lynknow.api.repository;

import com.lynknow.api.model.SubscriptionPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Integer> {

    @Query("FROM SubscriptionPackage WHERE id = :id AND isActive = 1")
    SubscriptionPackage getDetail(@Param("id") Integer id);

    @Query("SELECT subs FROM SubscriptionPackage subs " +
            "WHERE subs.isActive = 1 " +
            "AND subs.isShow = 1 " +
            "AND (LOWER(subs.description) LIKE %:param% " +
            "OR LOWER(subs.currency) LIKE %:param% " +
            "OR LOWER(subs.period) LIKE %:param%)")
    Page<SubscriptionPackage> getList(String param, Pageable pageable);

}
