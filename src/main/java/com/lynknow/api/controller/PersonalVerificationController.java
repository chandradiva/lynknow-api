package com.lynknow.api.controller;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.VerifyPersonalRequest;
import com.lynknow.api.service.PersonalVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/users/verifications")
public class PersonalVerificationController {

    @Autowired
    private PersonalVerificationService personalVerificationService;

    @PostMapping("request")
    public ResponseEntity requestToVerify(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "item", defaultValue = "1") Integer item,
            @RequestParam(name = "remarks", defaultValue = "") String remarks
    ) {
       return personalVerificationService.requestToVerify(file, remarks, item);
    }

    @PatchMapping("verify")
    public ResponseEntity verifyRequest(@RequestBody VerifyPersonalRequest request) {
        return personalVerificationService.verifyRequest(request);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        return personalVerificationService.getDetail(id);
    }

    @GetMapping("by-param")
    public ResponseEntity getDetail(
            @RequestParam Long userId,
            @RequestParam Integer itemId
    ) {
        return personalVerificationService.getDetail(userId, itemId);
    }

    @GetMapping("list/{userId}")
    public ResponseEntity getList(@PathVariable Long userId) {
        return personalVerificationService.getList(userId);
    }

    @GetMapping("need-verify")
    public ResponseEntity getListNeedVerify(PaginationModel myPage) {
        return personalVerificationService.getListNeedToVerify(myPage);
    }

}
