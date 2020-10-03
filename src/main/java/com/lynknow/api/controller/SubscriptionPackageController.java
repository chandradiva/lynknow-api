package com.lynknow.api.controller;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.pojo.request.StripeChargeRequest;
import com.lynknow.api.pojo.request.SubscriptionPackageRequest;
import com.lynknow.api.service.StripeService;
import com.lynknow.api.service.SubscriptionPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/packages")
public class SubscriptionPackageController {

    @Autowired
    private SubscriptionPackageService subscriptionPackageService;

    @Autowired
    private StripeService stripeService;

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

    @PostMapping("buy-package")
    public ResponseEntity buyPackage(@RequestBody StripeChargeRequest request) {
        return stripeService.createStripeCharge(request);
    }

    @GetMapping("retrieve-charge")
    public ResponseEntity retrieveCharge(@RequestParam String chargeId) {
        return stripeService.retrieveCharge(chargeId);
    }

}
