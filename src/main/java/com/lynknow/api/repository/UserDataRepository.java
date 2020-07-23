package com.lynknow.api.repository;

import com.lynknow.api.model.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

    @Query("FROM UserData WHERE id = :id AND isActive = 1")
    UserData getDetail(@Param("id") Long id);

    @Query("FROM UserData WHERE username = :username AND isActive = 1")
    UserData getByUsername(@Param("username") String username);

    @Query("FROM UserData WHERE email = :email AND isActive = 1")
    UserData getByEmail(@Param("email") String email);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.username) = :username " +
            "AND ud.isActive = 1")
    Page<UserData> getByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT ud FROM UserData ud " +
            "WHERE LOWER(ud.email) = :email " +
            "AND ud.isActive = 1")
    Page<UserData> getByEmail(@Param("email") String email, Pageable pageable);

}
