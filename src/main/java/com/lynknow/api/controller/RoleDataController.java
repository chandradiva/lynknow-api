package com.lynknow.api.controller;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.service.RoleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/roles")
public class RoleDataController {

    @Autowired
    private RoleDataService roleDataService;

    @GetMapping("list")
    public ResponseEntity getList() {
        return roleDataService.getList();
    }

    @GetMapping("")
    public ResponseEntity getListPagination(PaginationModel myPage) {
        return roleDataService.getListPagination(myPage);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Integer id) {
        return roleDataService.getDetail(id);
    }

}
