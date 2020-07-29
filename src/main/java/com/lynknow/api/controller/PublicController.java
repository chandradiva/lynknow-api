package com.lynknow.api.controller;

import com.lynknow.api.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(path = "public")
public class PublicController {

    @Autowired
    private UserCardService userCardService;

    @GetMapping("get-card")
    public ResponseEntity getCard(@RequestParam String code) {
        return userCardService.getDetailByCode(code);
    }

    @GetMapping("get-image")
    public byte[] getImage(@RequestParam String filename, HttpServletResponse httpResponse) throws IOException {
        return userCardService.getImageCard(filename, httpResponse);
    }

}
