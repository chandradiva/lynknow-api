package com.lynknow.api.repository;

import com.lynknow.api.model.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OtpTypeRepository extends JpaRepository<OtpType, Integer> {

    @Query("FROM OtpType WHERE id = :id")
    OtpType getDetail(@Param("id") Integer id);

    @Query("FROM OtpType ORDER BY id ASC")
    List<OtpType> getList();

}
