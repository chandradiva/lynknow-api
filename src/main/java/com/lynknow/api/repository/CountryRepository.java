package com.lynknow.api.repository;

import com.lynknow.api.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query("FROM Country WHERE id = :id")
    Country getDetail(@Param("id") Integer id);

    @Query("FROM Country WHERE LOWER(iso) = :iso")
    Country getDetail(@Param("iso") String iso);

    @Query("FROM Country WHERE LOWER(iso3) = :iso3")
    Country getDetailByIso3(@Param("iso3") String iso3);

    @Query("FROM Country WHERE LOWER(name) = :name")
    Country getDetailByName(@Param("name") String name);

    @Query("FROM Country WHERE phoneCode = :phoneCode")
    Country getDetailByPhoneCode(@Param("phoneCode") Integer phoneCode);

    @Query("FROM Country ORDER BY id ASC")
    List<Country> getList();

}
