package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.NotifyUpdatedCardRequest;
import com.lynknow.api.pojo.request.UserCardRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserCardService {

    ResponseEntity saveData(UserCardRequest request);
    ResponseEntity getList(Long userId, Integer typeId, Integer isPublished);
    ResponseEntity getListPagination(Long userId, Integer typeId, Integer isPublished, PaginationModel model);
    ResponseEntity getDetail(Long id);
    ResponseEntity deleteData(Long id);
    ResponseEntity publishCard(Long id, int type);

    ResponseEntity uploadFrontSide(MultipartFile file, Long id);
    ResponseEntity uploadFrontSideAws(MultipartFile file, Long id);

    ResponseEntity uploadBackSide(MultipartFile file, Long id);
    ResponseEntity uploadBackSideAws(MultipartFile file, Long id);

    ResponseEntity uploadProfilePicture(MultipartFile file, Long id);
    ResponseEntity uploadProfilePictureAws(MultipartFile file, Long id);

    byte[] getImageFrontSide(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageBackSide(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageProfilePicture(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageCard(String filename, HttpServletResponse httpResponse) throws IOException;

    ResponseEntity getDetailByCode(String code);
    ResponseEntity lockCard(Long id, int type);
    ResponseEntity checkCreateCard();

    ResponseEntity getDetail(Long id, int typeId);
    byte[] downloadContact(Long id, HttpServletResponse httpResponse) throws IOException;
    ResponseEntity<Resource> downloadContactResource(Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException;
    ResponseEntity requestToViewCard(String code);
    ResponseEntity grantRequest(Long requestId, int flag);
    ResponseEntity getDetailLockedCard(Long id);
    ResponseEntity getDetailLockedCard(String code);

    ResponseEntity getDetailCheckSession(Long id);
    ResponseEntity getDetailCheckSession(String code);

    ResponseEntity exchangeCard(Long fromCardId, Long exchangeCardId, Long exchangeUserId);
    ResponseEntity updateAllCode();

    ResponseEntity sendNotifyUpdateCard(Long id, NotifyUpdatedCardRequest request);

}
