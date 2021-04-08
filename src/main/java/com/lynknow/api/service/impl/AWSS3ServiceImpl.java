package com.lynknow.api.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.lynknow.api.LynknowApiApplication;
import com.lynknow.api.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Date;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${app.awsServices.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            String url = uploadFileToS3Bucket(bucketName, file);
            file.delete();

            return url;
        } catch (final AmazonServiceException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        String jarDir = System.getProperty("user.dir");
        CodeSource codeSource = LynknowApiApplication.class.getProtectionDomain().getCodeSource();

        try {
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            jarDir = jarFile.getParentFile().getPath();

            final File file = new File(jarDir + multipartFile.getOriginalFilename());
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(multipartFile.getBytes());
            outputStream.close();

            return file;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String uploadFileToS3Bucket(final String bucketName, final File file) {
        final String uniqueFileName = new Date().getTime() + "_" + file.getName();
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(bucketName, uniqueFileName).toString();
    }

}
