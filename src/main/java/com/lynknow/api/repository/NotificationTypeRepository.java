package com.lynknow.api.repository;

import com.lynknow.api.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {

    @Query("FROM NotificationType WHERE id = :id")
    NotificationType getDetail(@Param("id") Integer id);

    @Query("FROM NotificationType ORDER BY id ASC")
    List<NotificationType> getList();

}
