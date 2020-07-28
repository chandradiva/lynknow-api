package com.lynknow.api.controller;

import com.lynknow.api.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Integer id) {
        return countryService.getDetail(id);
    }

    @GetMapping("list")
    public ResponseEntity getList() {
        return countryService.getList();
    }

}
