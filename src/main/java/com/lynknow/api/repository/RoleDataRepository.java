package com.lynknow.api.repository;

import com.lynknow.api.model.RoleData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleDataRepository extends JpaRepository<RoleData, Integer> {

    @Query("FROM RoleData WHERE id = :id")
    RoleData getDetail(@Param("id") Integer id);

    @Query("FROM RoleData ORDER BY id ASC")
    List<RoleData> getList();

    @Query("SELECT role " +
            "FROM RoleData role " +
            "WHERE LOWER(role.name) LIKE %:param%")
    Page<RoleData> getListPagination(@Param("param") String param, Pageable pageable);

}
