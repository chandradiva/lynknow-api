package com.lynknow.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

    String uploadFile(MultipartFile multipartFile);
    void deleteFile(final String keyName);

}
