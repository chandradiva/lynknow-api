package com.lynknow.api.repository;

import com.lynknow.api.model.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Integer> {

    @Query("FROM SubscriptionPackage WHERE id = :id AND isActive = 1")
    SubscriptionPackage getDetail(@Param("id") Integer id);

}
