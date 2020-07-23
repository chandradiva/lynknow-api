package com.lynknow.api.controller;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.pojo.request.UserCardRequest;
import com.lynknow.api.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/cards")
public class UserCardController {

    @Autowired
    private UserCardService userCardService;

    @PostMapping("")
    public ResponseEntity saveData(@RequestBody UserCardRequest request) {
        return userCardService.saveData(request);
    }

    @PutMapping("{id}")
    public ResponseEntity updateData(@PathVariable Long id, @RequestBody UserCardRequest request) {
        request.setId(id);
        return userCardService.saveData(request);
    }

    @GetMapping("list")
    public ResponseEntity getList(
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "isPublished", required = false, defaultValue = "-1") Integer isPublished
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return userCardService.getList(userSession.getId(), typeId, isPublished);
    }

    @GetMapping("")
    public ResponseEntity getListPagination(
            PaginationModel myPage,
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "isPublished", required = false, defaultValue = "-1") Integer isPublished,
            @RequestParam(name = "param", required = false, defaultValue = "") String param
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return userCardService.getListPagination(userSession.getId(), typeId, isPublished, myPage);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        return userCardService.getDetail(id);
    }

    @DeleteMapping("")
    public ResponseEntity deleteData(@RequestBody DeleteDataRequest request) {
        return userCardService.deleteData(request.getId());
    }

    @PatchMapping("{id}/publish")
    public ResponseEntity publishData(@PathVariable Long id) {
        return userCardService.publishCard(id, 1);
    }

    @PatchMapping("{id}/unpublish")
    public ResponseEntity unpublishData(@PathVariable Long id) {
        return userCardService.publishCard(id, 2);
    }

}
