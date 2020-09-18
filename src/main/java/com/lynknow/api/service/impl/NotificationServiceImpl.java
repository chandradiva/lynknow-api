package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.Notification;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.NotificationResponse;
import com.lynknow.api.repository.NotificationRepository;
import com.lynknow.api.repository.NotificationTypeRepository;
import com.lynknow.api.service.NotificationService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private NotificationTypeRepository notificationTypeRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getDetail(Long id) {
        try {
            Notification notification = notificationRepo.getDetail(id);
            if (notification != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseNotification(notification)), HttpStatus.OK);
            } else {
                LOGGER.error("Notification ID: " + id + " is not found");
                throw new NotFoundException("Notification ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPagination(Long userId, Long targetUserId, Integer typeId, Integer isRead, PaginationModel model) {
        try {
            Page<NotificationResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = notificationRepo.getListPagination(
                        userId,
                        targetUserId,
                        typeId == 0 ? null : typeId,
                        isRead == -1 ? null : isRead,
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy()))
                ).map(generateRes::generateResponseNotification);
            } else {
                page = notificationRepo.getListPagination(
                        userId,
                        targetUserId,
                        typeId == 0 ? null : typeId,
                        isRead == -1 ? null : isRead,
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy()))
                ).map(generateRes::generateResponseNotification);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity markAsRead(Long id) {
        try {
            Notification notification = notificationRepo.getDetail(id);
            if (notification != null) {
                notification.setIsRead(1);
                notification.setUpdatedDate(new Date());

                notificationRepo.save(notification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseNotification(notification)), HttpStatus.OK);
            } else {
                LOGGER.error("Notification ID: " + id + " is not found");
                throw new NotFoundException("Notification ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity deleteData(Long id) {
        try {
            Notification notification = notificationRepo.getDetail(id);
            if (notification != null) {
                notification.setIsActive(0);
                notification.setDeletedDate(new Date());

                notificationRepo.save(notification);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Notification ID: " + id + " is not found");
                throw new NotFoundException("Notification ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity markAsRead(List<Long> ids) {
        try {
            int result = notificationRepo.markAsRead(ids);
            if (result == -1) {
                LOGGER.error("Cannot Execute Query Update Data");
                throw new InternalServerErrorException("Cannot Execute Query Update Data");
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    result), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity countUnread(Long userId) {
        try {
            long result = notificationRepo.countUnread(userId);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    result), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
