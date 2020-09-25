package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.SubscriptionPackageDetail;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.request.SubscriptionPackageRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.SubscriptionPackageResponse;
import com.lynknow.api.repository.SubscriptionPackageDetailRepository;
import com.lynknow.api.repository.SubscriptionPackageRepository;
import com.lynknow.api.service.SubscriptionPackageService;
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

import java.util.Date;

@Service
public class SubscriptionPackageServiceImpl implements SubscriptionPackageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionPackageServiceImpl.class);

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepo;

    @Autowired
    private SubscriptionPackageDetailRepository subscriptionPackageDetailRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity saveData(SubscriptionPackageRequest request) {
        try {
            if (request.getId() == null) {
                // new data
                SubscriptionPackage subs = new SubscriptionPackage();

                subs.setName(request.getName());
                subs.setDescription(request.getDescription());
                subs.setPrice(request.getPrice());
                subs.setCurrency(request.getCurrency());
                subs.setPeriod(request.getPeriod());
                subs.setInterval(request.getInterval());
                subs.setImageUrl(request.getImageUrl());
                subs.setCreatedDate(new Date());
                subs.setIsActive(1);

                subscriptionPackageRepo.save(subs);

                for (String item : request.getDetails()) {
                    SubscriptionPackageDetail detail = new SubscriptionPackageDetail();

                    detail.setSubscriptionPackage(subs);
                    detail.setDescription(item);

                    subscriptionPackageDetailRepo.save(detail);
                }

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        201,
                        "Created",
                        generateRes.generateResponseSubscription(subs)), HttpStatus.CREATED);
            } else {
                // update data
                SubscriptionPackage subs = subscriptionPackageRepo.getDetail(request.getId());
                if (subs != null) {
                    subs.setName(request.getName());
                    subs.setDescription(request.getDescription());
                    subs.setPrice(request.getPrice());
                    subs.setCurrency(request.getCurrency());
                    subs.setPeriod(request.getPeriod());
                    subs.setInterval(request.getInterval());
                    subs.setImageUrl(request.getImageUrl());
                    subs.setUpdatedDate(new Date());

                    subscriptionPackageRepo.save(subs);

                    subscriptionPackageDetailRepo.delete(request.getId());
                    for (String item : request.getDetails()) {
                        SubscriptionPackageDetail detail = new SubscriptionPackageDetail();

                        detail.setSubscriptionPackage(subs);
                        detail.setDescription(item);

                        subscriptionPackageDetailRepo.save(detail);
                    }

                    return new ResponseEntity(new BaseResponse<>(
                            true,
                            200,
                            "Success",
                            generateRes.generateResponseSubscription(subs)), HttpStatus.OK);
                } else {
                    LOGGER.error("Subscription Package ID: " + request.getId() + " is not found");
                    throw new NotFoundException("Subscription Package ID: " + request.getId());
                }
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getDetail(Integer id) {
        try {
            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(id);
            if (subs != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseSubscription(subs)), HttpStatus.OK);
            } else {
                LOGGER.error("Subscription Package ID: " + id + " is not found");
                throw new NotFoundException("Subscription Package ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList(PaginationModel model) {
        try {
            Page<SubscriptionPackageResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = subscriptionPackageRepo.getList(
                        model.getParam(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy())))
                        .map(generateRes::generateResponseSubscription);
            } else {
                page = subscriptionPackageRepo.getList(
                        model.getParam(),
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy())))
                        .map(generateRes::generateResponseSubscription);
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    page), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity deleteData(Integer id) {
        try {
            SubscriptionPackage subs = subscriptionPackageRepo.getDetail(id);
            if (subs != null) {
                subs.setIsActive(0);
                subs.setDeletedDate(new Date());

                subscriptionPackageRepo.save(subs);

                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        null), HttpStatus.OK);
            } else {
                LOGGER.error("Subscription Package ID: " + id + " is not found");
                throw new NotFoundException("Subscription Package ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
