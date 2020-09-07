package com.lynknow.api.controller;

import com.lynknow.api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "dashboards")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("total-user")
    public ResponseEntity getDataTotalUser() {
        return dashboardService.getDataTotalUser();
    }

    @GetMapping("total-card")
    public ResponseEntity getDataTotalCard() {
        return dashboardService.getDataTotalCard();
    }

    @GetMapping("total-card-verification")
    public ResponseEntity getDataTotalCardVerification() {
        return dashboardService.getDataTotalCardVerification();
    }

    @GetMapping("total-personal-verification")
    public ResponseEntity getDataTotalPersonalVerification() {
        return dashboardService.getDataTotalPersonalVerification();
    }

}
