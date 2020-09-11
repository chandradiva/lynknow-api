package com.lynknow.api.controller;

import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.RoleDataRepository;
import com.lynknow.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RoleDataRepository roleDataRepo;

    @GetMapping("check")
    public ResponseEntity testApi() {
        LOGGER.error("ERROR LOG");
        LOGGER.debug("DEBUG LOG");
        LOGGER.info("INFO LOG");

        return new ResponseEntity(new BaseResponse<>(
                true,
                200,
                "Success",
                StringUtil.generateUniqueCodeCard()), HttpStatus.OK);
    }

    @GetMapping("check-db")
    public ResponseEntity testDb() {
        return new ResponseEntity(new BaseResponse<>(
                true,
                200,
                "Success",
                roleDataRepo.getList()), HttpStatus.OK);
    }

}
