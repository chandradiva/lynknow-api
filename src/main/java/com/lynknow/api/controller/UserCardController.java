package com.lynknow.api.controller;

import com.lynknow.api.exception.BadRequestException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.DeleteDataRequest;
import com.lynknow.api.pojo.request.UserCardRequest;
import com.lynknow.api.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(path = "/users/cards")
public class UserCardController {

    @Autowired
    private UserCardService userCardService;

    @PostMapping("")
    public ResponseEntity saveData(@RequestBody UserCardRequest request) {
        return userCardService.saveData(request);
    }

    @PutMapping("{id}")
    public ResponseEntity updateData(@PathVariable Long id, @RequestBody UserCardRequest request) {
        request.setId(id);
        return userCardService.saveData(request);
    }

    @GetMapping("list")
    public ResponseEntity getList(
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "isPublished", required = false, defaultValue = "-1") Integer isPublished
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return userCardService.getList(userSession.getId(), typeId, isPublished);
    }

    @GetMapping("")
    public ResponseEntity getListPagination(
            PaginationModel myPage,
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "isPublished", required = false, defaultValue = "-1") Integer isPublished,
            @RequestParam(name = "param", required = false, defaultValue = "") String param
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData userSession = (UserData) auth.getPrincipal();

        return userCardService.getListPagination(userSession.getId(), typeId, isPublished, myPage);
    }

    @GetMapping("{id}")
    public ResponseEntity getDetail(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") Integer type
    ) {
        if (type == 0) {
            return userCardService.getDetail(id);
        } else {
            return userCardService.getDetail(id, type);
        }
    }

    @DeleteMapping("")
    public ResponseEntity deleteData(@RequestBody DeleteDataRequest request) {
        return userCardService.deleteData(request.getId());
    }

    @PatchMapping("{id}/publish")
    public ResponseEntity publishData(@PathVariable Long id) {
        return userCardService.publishCard(id, 1);
    }

    @PatchMapping("{id}/unpublish")
    public ResponseEntity unpublishData(@PathVariable Long id) {
        return userCardService.publishCard(id, 2);
    }

    @PostMapping("upload")
    public ResponseEntity uploadData(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "id", required = false, defaultValue = "0") Long id,
            @RequestParam(name = "type", defaultValue = "1") Integer type
    ) {
        if (type == 1) {
            return userCardService.uploadFrontSide(file, id);
        } else if (type == 2) {
            return userCardService.uploadBackSide(file, id);
        } else if (type == 3) {
            return userCardService.uploadProfilePicture(file, id);
        } else {
            throw new BadRequestException();
        }
    }

    @GetMapping(value = "get-image", produces = {"image/png", "image/jpg"})
    public byte[] getImage(
            @RequestParam String filename,
            @RequestParam(name = "type", defaultValue = "1") Integer type,
            HttpServletResponse httpResponse
    ) throws IOException {
        if (type == 1) {
            return userCardService.getImageFrontSide(filename, httpResponse);
        } else if (type == 2) {
            return userCardService.getImageBackSide(filename, httpResponse);
        } else if (type == 3) {
            return userCardService.getImageProfilePicture(filename, httpResponse);
        } else {
            throw new BadRequestException();
        }
    }

    @PatchMapping("{id}/lock")
    public ResponseEntity lockCard(@PathVariable Long id) {
        return userCardService.lockCard(id, 1);
    }

    @PatchMapping("{id}/unlock")
    public ResponseEntity unlockCard(@PathVariable Long id) {
        return userCardService.lockCard(id, 2);
    }

    @GetMapping("check-create")
    public ResponseEntity checkCreateCard() {
        return userCardService.checkCreateCard();
    }

    @GetMapping("detail")
    public ResponseEntity getDetailWithType(
            @RequestParam Long id,
            @RequestParam Integer type
    ) {
        return userCardService.getDetail(id, type);
    }

    @GetMapping(value = "download-contact")
    public byte[] downloadContact(@RequestParam Long id, HttpServletResponse httpResponse) throws IOException {
        return userCardService.downloadContact(id, httpResponse);
    }

    @PostMapping("request-to-view")
    public ResponseEntity requestToViewCard(@RequestParam String code) {
        return userCardService.requestToViewCard(code);
    }

    @PatchMapping("grant-request")
    public ResponseEntity grantRequestToViewCard(@RequestParam Long requestId) {
        return userCardService.grantRequest(requestId, 1);
    }

    @PatchMapping("reject-request")
    public ResponseEntity rejectRequestToViewCard(@RequestParam Long requestId) {
        return userCardService.grantRequest(requestId, 2);
    }

    @GetMapping("detail-locked")
    public ResponseEntity getDetailLocked(@RequestParam Long id) {
        return userCardService.getDetailLockedCard(id);
    }

    @GetMapping("detail-locked-by-code")
    public ResponseEntity getDetailLockedByCode(@RequestParam String code) {
        return userCardService.getDetailLockedCard(code);
    }

    @GetMapping("detail-own")
    public ResponseEntity getDetailOwn(@RequestParam Long id) {
        return userCardService.getDetailCheckSession(id);
    }

    @GetMapping("detail-own-by-code")
    public ResponseEntity getDetailOwn(@RequestParam String code) {
        return userCardService.getDetailCheckSession(code);
    }

    @PostMapping("exchange")
    public ResponseEntity exchangeCard(
            @RequestParam Long fromCardId,
            @RequestParam Long exchangeCardId
    ) {
        return userCardService.exchangeCard(fromCardId, exchangeCardId);
    }

    @PatchMapping("update-all-code")
    public ResponseEntity updateAllCode() {
        return userCardService.updateAllCode();
    }

}
