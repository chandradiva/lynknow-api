package com.lynknow.api.controller;

import com.lynknow.api.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "card-types")
public class CardTypeController {

    @Autowired
    private CardTypeService cardTypeService;

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Integer id) {
        return cardTypeService.getDetail(id);
    }

    @GetMapping("list")
    public ResponseEntity getList() {
        return cardTypeService.getList();
    }

}
