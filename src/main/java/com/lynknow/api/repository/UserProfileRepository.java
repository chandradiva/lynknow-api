package com.lynknow.api.repository;

import com.lynknow.api.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("FROM UserProfile WHERE userData.id = :userId")
    UserProfile getDetailByUserId(@Param("userId") Long userId);

}
