package com.lynknow.api.controller;

import com.lynknow.api.pojo.request.VerifyCardRequest;
import com.lynknow.api.service.CardVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/users/cards/verifications")
public class CardVerificationController {

    @Autowired
    private CardVerificationService cardVerificationService;

    @GetMapping("init")
    public ResponseEntity initVerification(@RequestParam Long cardId) {
        return cardVerificationService.initCardVerification(cardId);
    }

    @PostMapping("{cardId}/request")
    public ResponseEntity requestToVerify(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "item", defaultValue = "1") Integer item,
            @PathVariable Long cardId
    ) {
        return cardVerificationService.requestToVerify(file, cardId, item);
    }

    @PostMapping("{cardId}/request-phone")
    public ResponseEntity requestToVerify(
            @RequestParam String officePhone,
            @PathVariable Long cardId
    ) {
        return cardVerificationService.requestToVerify(officePhone, cardId);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        return cardVerificationService.getDetail(id);
    }

    @GetMapping("by-param")
    public ResponseEntity getDetail(@RequestParam Long cardId, @RequestParam Integer item) {
        return cardVerificationService.getDetail(cardId, item);
    }

    @GetMapping("{cardId}/list")
    public ResponseEntity getList(@PathVariable Long cardId) {
        return cardVerificationService.getList(cardId);
    }

    @PatchMapping("verify")
    public ResponseEntity verifyRequest(@RequestBody VerifyCardRequest request) {
        return cardVerificationService.verifyRequest(request);
    }

}