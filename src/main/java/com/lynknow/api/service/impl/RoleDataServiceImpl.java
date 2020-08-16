package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.RoleData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.RoleDataResponse;
import com.lynknow.api.repository.RoleDataRepository;
import com.lynknow.api.service.RoleDataService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleDataServiceImpl implements RoleDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleDataServiceImpl.class);

    @Autowired
    private RoleDataRepository roleDataRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getList() {
        try {
            List<RoleDataResponse> datas = new ArrayList<>();
            List<RoleData> roles = roleDataRepo.getList();
            if (roles != null) {
                for (RoleData role : roles) {
                    datas.add(generateRes.generateResponseRole(role));
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
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListPagination(PaginationModel model) {
        try {
            Page<RoleDataResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = roleDataRepo.getListPagination(
                        model.getParam(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy())))
                        .map(generateRes::generateResponseRole);
            } else {
                page = roleDataRepo.getListPagination(
                        model.getParam(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy())))
                        .map(generateRes::generateResponseRole);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Integer id) {
        try {
            RoleData role = roleDataRepo.getDetail(id);
            if (role != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseRole(role)), HttpStatus.OK);
            } else {
                LOGGER.error("Role ID: " + id + " is not found");
                throw new NotFoundException("Role ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data" + e.getMessage());
        }
    }

}
