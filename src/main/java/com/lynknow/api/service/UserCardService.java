package com.lynknow.api.service;

import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.UserCardRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserCardService {

    ResponseEntity saveData(UserCardRequest request);
    ResponseEntity getList(Long userId, Integer typeId, Integer isPublished);
    ResponseEntity getListPagination(Long userId, Integer typeId, Integer isPublished, PaginationModel model);
    ResponseEntity getDetail(Long id);
    ResponseEntity deleteData(Long id);
    ResponseEntity publishCard(Long id, int type);

    ResponseEntity uploadFrontSide(MultipartFile file, Long id);
    ResponseEntity uploadBackSide(MultipartFile file, Long id);
    ResponseEntity uploadProfilePicture(MultipartFile file, Long id);

    byte[] getImageFrontSide(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageBackSide(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageProfilePicture(String filename, HttpServletResponse httpResponse) throws IOException;
    byte[] getImageCard(String filename, HttpServletResponse httpResponse) throws IOException;

    ResponseEntity getDetailByCode(String code);
    ResponseEntity lockCard(Long id, int type);
    ResponseEntity checkCreateCard();

}
