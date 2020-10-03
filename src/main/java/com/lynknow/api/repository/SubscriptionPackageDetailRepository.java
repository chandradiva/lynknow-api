package com.lynknow.api.repository;

import com.lynknow.api.model.SubscriptionPackageDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubscriptionPackageDetailRepository extends JpaRepository<SubscriptionPackageDetail, Integer> {

    @Query("FROM SubscriptionPackageDetail WHERE id = :id")
    SubscriptionPackageDetail getDetail(@Param("id") Integer id);

    @Query("FROM SubscriptionPackageDetail WHERE subscriptionPackage.id = :packageId ORDER BY id ASC")
    List<SubscriptionPackageDetail> getList(@Param("packageId") Integer packageId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SubscriptionPackageDetail WHERE subscriptionPackage.id = :packageId")
    int deleteData(@Param("packageId") Integer packageId);

}
