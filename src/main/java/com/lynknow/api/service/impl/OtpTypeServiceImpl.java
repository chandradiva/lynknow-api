package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.OtpType;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.OtpTypeResponse;
import com.lynknow.api.repository.OtpTypeRepository;
import com.lynknow.api.service.OtpTypeService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtpTypeServiceImpl implements OtpTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleDataServiceImpl.class);

    @Autowired
    private OtpTypeRepository otpTypeRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getDetail(Integer id) {
        try {
            OtpType type = otpTypeRepo.getDetail(id);
            if (type != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseOtpType(type)), HttpStatus.OK);
            } else {
                LOGGER.error("OTP Type ID: " + id + " is not found");
                throw new NotFoundException("OTP Type ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList() {
        try {
            List<OtpTypeResponse> datas = new ArrayList<>();
            List<OtpType> types = otpTypeRepo.getList();
            if (types != null) {
                for (OtpType item : types) {
                    datas.add(generateRes.generateResponseOtpType(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    datas), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
