package com.lynknow.api.controller;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.service.UserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/contacts")
public class UserContactController {

    @Autowired
    private UserContactService userContactService;

    @GetMapping("contact")
    public ResponseEntity getListPagination(PaginationModel myPage) {
        return userContactService.getListPaginationContact(myPage);
    }

    @PatchMapping("accept")
    public ResponseEntity acceptRequest(@RequestParam Long id) {
        return userContactService.updateStatus(id, 1);
    }

    @PatchMapping("reject")
    public ResponseEntity rejectRequest(@RequestParam Long id) {
        return userContactService.updateStatus(id, 2);
    }

    @PatchMapping("cancel")
    public ResponseEntity cancelRequest(@RequestParam Long id) {
        return userContactService.updateStatus(id, 3);
    }

    @GetMapping("received")
    public ResponseEntity getListPaginationReceived(PaginationModel myPage) {
        return userContactService.getListPaginationReceived(myPage);
    }

}
