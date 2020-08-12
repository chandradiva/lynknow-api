package com.lynknow.api.repository;

import com.lynknow.api.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("FROM Notification WHERE id = :id AND isActive = 1")
    Notification getDetail(@Param("id") Long id);

    @Query("SELECT nf FROM Notification nf " +
            "WHERE nf.isActive = 1 " +
            "AND (:userId IS NULL OR nf.userData.id = :userId) " +
            "AND (:targetUserId IS NULL OR nf.targetUserData.id = :targetUserId) " +
            "AND (:typeId IS NULL OR nf.notificationType.id = :typeId) " +
            "AND (:isRead IS NULL OR nf.isRead = :isRead)")
    Page<Notification> getListPagination(
            @Param("userId") Long userId,
            @Param("targetUserId") Long targetUserId,
            @Param("typeId") Integer typeId,
            @Param("isRead") Integer isRead,
            Pageable pageable);

}
