package com.lynknow.api.controller;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity getListPagination(
            PaginationModel myPage,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "isRead", required = false, defaultValue = "-1") Integer isRead
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return notificationService.getListPagination(
                null,
                userSession.getId(),
                typeId,
                isRead,
                myPage
        );
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        return notificationService.getDetail(id);
    }

    @PatchMapping("read")
    public ResponseEntity markAsRead(@RequestParam Long id) {
        return notificationService.markAsRead(id);
    }

    @DeleteMapping("")
    public ResponseEntity deleteData(@RequestBody DeleteDataRequest request) {
        return notificationService.deleteData(request.getId());
    }

}
