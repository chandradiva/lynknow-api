package com.lynknow.api.repository;

import com.lynknow.api.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("FROM Notification WHERE id = :id AND isActive = 1")
    Notification getDetail(@Param("id") Long id);

    @Query("SELECT nf FROM Notification nf " +
            "WHERE nf.isActive = 1 " +
            "AND (:userId IS NULL OR nf.userData.id = :userId) " +
            "AND (:targetUserId IS NULL OR nf.targetUserData.id = :targetUserId) " +
            "AND (:targetCardId IS NULL OR nf.targetUserCard.id = :targetCardId) " +
            "AND (:typeId IS NULL OR nf.notificationType.id = :typeId) " +
            "AND (:isRead IS NULL OR nf.isRead = :isRead)")
    Page<Notification> getListPagination(
            @Param("userId") Long userId,
            @Param("targetUserId") Long targetUserId,
            @Param("targetCardId") Long targetCardId,
            @Param("typeId") Integer typeId,
            @Param("isRead") Integer isRead,
            Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Notification SET isRead = 1 WHERE isRead = 0 AND id IN (:ids)")
    int markAsRead(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE Notification SET isRead = 1 WHERE targetUserData.id = :userId AND isRead = 0")
    int markAsReadByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(nf) FROM Notification nf " +
            "WHERE nf.targetUserData.id = :userId " +
            "AND nf.isRead = 0")
    long countUnread(@Param("userId") Long userId);

    @Query(value = "SELECT nt.id, nt.\"name\", COUNT(nt.id) FROM notification_type nt " +
            "LEFT JOIN notification n ON nt.id = n.notification_type_id " +
            "WHERE n.target_user_data_id = :userId " +
            "AND n.target_user_card_id = :cardId " +
            "AND n.created_date BETWEEN :start AND :end " +
            "GROUP BY nt.id " +
            "ORDER BY nt.id", nativeQuery = true)
    List<Object[]> getStatistic(
            @Param("userId") Long userId,
            @Param("cardId") Long cardId,
            @Param("start") Date start,
            @Param("end") Date end);

}
