package com.lynknow.api.repository;

import com.lynknow.api.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

    @Query("FROM UserData WHERE id = :id AND isActive = 1")
    UserData getDetail(@Param("id") Long id);

    @Query("FROM UserData WHERE username = :username AND isActive = 1")
    UserData getByUsername(@Param("username") String username);

}
