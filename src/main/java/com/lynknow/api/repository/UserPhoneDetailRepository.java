package com.lynknow.api.repository;

import com.lynknow.api.model.UserPhoneDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPhoneDetailRepository extends JpaRepository<UserPhoneDetail, Long> {

    @Query("FROM UserPhoneDetail WHERE id = :id")
    UserPhoneDetail getDetail(@Param("id") Long id);

    @Query("FROM UserPhoneDetail WHERE userProfile.id = :profileId AND type = :type")
    UserPhoneDetail getDetail(@Param("profileId") Long profileId, @Param("type") int type);

    @Query("SELECT det FROM UserPhoneDetail det WHERE det.userProfile.id = :profileId AND det.type = :type")
    Page<UserPhoneDetail> getDetail(@Param("profileId") Long profileId, @Param("type") int type, Pageable pageable);

}
