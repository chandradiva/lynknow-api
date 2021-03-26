package com.lynknow.api.controller;

import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.service.CardViewersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "card-viewers")
public class CardViewersController {

    @Autowired
    private CardViewersService cardViewersService;

    @GetMapping("")
    public ResponseEntity getListPagination(PaginationModel myPage) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return cardViewersService.getListPagination(userSession.getId(), myPage);
    }

}
