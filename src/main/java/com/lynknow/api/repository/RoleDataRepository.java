package com.lynknow.api.repository;

import com.lynknow.api.model.RoleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleDataRepository extends JpaRepository<RoleData, Integer> {

    @Query("FROM RoleData ORDER BY id ASC")
    List<RoleData> getList();

}
