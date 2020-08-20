package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.CardType;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.CardTypeResponse;
import com.lynknow.api.repository.CardTypeRepository;
import com.lynknow.api.service.CardTypeService;
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
public class CardTypeServiceImpl implements CardTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardTypeServiceImpl.class);

    @Autowired
    private CardTypeRepository cardTypeRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getDetail(Integer id) {
        try {
            CardType type = cardTypeRepo.getDetail(id);
            if (type != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCardType(type)), HttpStatus.OK);
            } else {
                LOGGER.error("Card Type ID: " + id + " is not found");
                throw new NotFoundException("Card Type ID: " + id);
            }
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getList() {
        try {
            List<CardTypeResponse> datas = new ArrayList<>();
            List<CardType> types = cardTypeRepo.getList();
            if (types != null) {
                for (CardType item : types) {
                    datas.add(generateRes.generateResponseCardType(item));
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    datas), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

}
