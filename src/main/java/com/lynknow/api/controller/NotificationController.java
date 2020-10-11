package com.lynknow.api.controller;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.pojo.request.MarkAsReadBulkRequest;
import com.lynknow.api.service.NotificationService;
import com.lynknow.api.service.StatisticPageService;
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

    @Autowired
    private StatisticPageService statisticPageService;

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

    @PatchMapping("read-bulk")
    public ResponseEntity markAsRead(@RequestBody MarkAsReadBulkRequest request) {
        return notificationService.markAsRead(request.getIds());
    }

    @GetMapping("count-unread")
    public ResponseEntity countUnread() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return notificationService.countUnread(userSession.getId());
    }

    @GetMapping("get-statistic")
    public ResponseEntity getStatistic(@RequestParam Long cardId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return statisticPageService.getStatistic(userSession.getId(), cardId);
    }

    @GetMapping("get-statistic-details")
    public ResponseEntity getStatisticDetails(
            PaginationModel myPage,
            @RequestParam Long cardId,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return statisticPageService.getListDetail(userSession.getId(), cardId, typeId, myPage);
    }

    @PatchMapping("read-all")
    public ResponseEntity markAsReadByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return notificationService.markAsReadByUser(userSession.getId());
    }

}
