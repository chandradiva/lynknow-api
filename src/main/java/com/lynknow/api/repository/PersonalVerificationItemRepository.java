package com.lynknow.api.repository;

import com.lynknow.api.model.PersonalVerificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalVerificationItemRepository extends JpaRepository<PersonalVerificationItem, Integer> {

    @Query("FROM PersonalVerificationItem WHERE id = :id")
    PersonalVerificationItem getDetail(@Param("id") Integer id);

    @Query("FROM PersonalVerificationItem ORDER BY id ASC")
    List<PersonalVerificationItem> getList();

}
