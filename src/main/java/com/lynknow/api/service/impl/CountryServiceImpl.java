package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.NotFoundException;
import com.lynknow.api.model.Country;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.CountryResponse;
import com.lynknow.api.repository.CountryRepository;
import com.lynknow.api.service.CountryService;
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
public class CountryServiceImpl implements CountryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getList() {
        try {
            List<CountryResponse> datas = new ArrayList<>();
            List<Country> countries = countryRepo.getList();
            if (countries != null) {
                for (Country item : countries) {
                    datas.add(generateRes.generateResponseCountry(item));
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
    public ResponseEntity getDetail(Integer id) {
        try {
            Country country = countryRepo.getDetail(id);
            if (country != null) {
                return new ResponseEntity(new BaseResponse<>(
                        true,
                        200,
                        "Success",
                        generateRes.generateResponseCountry(country)), HttpStatus.OK);
            } else {
                LOGGER.error("Country ID: " + id + " is not found");
                throw new NotFoundException("Country ID: " + id);
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
