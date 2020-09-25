package com.lynknow.api.controller;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.pojo.request.SubscriptionPackageRequest;
import com.lynknow.api.service.SubscriptionPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/packages")
public class SubscriptionPackageController {

    @Autowired
    private SubscriptionPackageService subscriptionPackageService;

    @PostMapping("")
    public ResponseEntity saveData(@RequestBody SubscriptionPackageRequest request) {
        return subscriptionPackageService.saveData(request);
    }

    @PutMapping("{id}")
    public ResponseEntity updateData(@PathVariable Integer id, @RequestBody SubscriptionPackageRequest request) {
        request.setId(id);
        return subscriptionPackageService.saveData(request);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Integer id) {
        return subscriptionPackageService.getDetail(id);
    }

    @GetMapping("")
    public ResponseEntity getList(PaginationModel myPage) {
        return subscriptionPackageService.getList(myPage);
    }

    @DeleteMapping("")
    public ResponseEntity deleteData(@RequestBody DeleteDataRequest request) {
        return subscriptionPackageService.deleteData(request.getId().intValue());
    }

}
