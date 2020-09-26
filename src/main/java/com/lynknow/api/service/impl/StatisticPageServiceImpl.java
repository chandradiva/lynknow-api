package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.NotificationResponse;
import com.lynknow.api.pojo.response.StatisticPageResponse;
import com.lynknow.api.repository.NotificationRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.StatisticPageService;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StatisticPageServiceImpl implements StatisticPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticPageServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Override
    public ResponseEntity getStatistic(Long userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            Calendar calStart = Calendar.getInstance();
            if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                calStart.add(Calendar.YEAR, -1);
            } else {
                calStart.add(Calendar.MONTH, -1);
            }

            calStart.add(Calendar.DAY_OF_MONTH, 1);
            calStart.set(Calendar.HOUR_OF_DAY, 0);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 1);
            Date start = calStart.getTime();

            Calendar calEnd = Calendar.getInstance();
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 59);
            Date end = calEnd.getTime();

            List<StatisticPageResponse> stats = this.initStatistic();
            List<Object[]> objects = notificationRepo.getStatistic(userId, start, end);
            List<StatisticPageResponse> temp = this.convertFrom(objects);

            for (StatisticPageResponse stat : stats) {
                for (StatisticPageResponse item : temp) {
                    if (stat.getId() == item.getId()) {
                        stat.setName(item.getName());
                        stat.setStats(item.getStats());
                    }
                }
            }

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    stats), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity getListDetail(Long userId, Integer typeId, PaginationModel model) {
        try {
            Page<NotificationResponse> page = null;
            if (model.getSort().equals("asc")) {
                page = notificationRepo.getListPagination(
                        null,
                        userId,
                        typeId == 0 ? null : typeId,
                        null,
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.ASC, model.getSortBy()))
                ).map(generateRes::generateResponseNotification);
            } else {
                page = notificationRepo.getListPagination(
                        null,
                        userId,
                        typeId == 0 ? null : typeId,
                        null,
                        PageRequest.of(model.getPage(), model.getSize(), Sort.by(Sort.Direction.DESC, model.getSortBy()))
                ).map(generateRes::generateResponseNotification);
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

    private List<StatisticPageResponse> initStatistic() {
        List<StatisticPageResponse> data = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            StatisticPageResponse stat = new StatisticPageResponse();

            stat.setId(i);
            stat.setStats(0);

            if (i == 1) {
                stat.setName("View Profile Picture");
            } else if (i == 2) {
                stat.setName("View Mobile Number");
            } else if (i == 3) {
                stat.setName("SMS");
            } else if (i == 4) {
                stat.setName("View WhatsApp Number");
            } else if (i == 5) {
                stat.setName("View Email");
            } else if (i == 6) {
                stat.setName("Exchange Card");
            } else if (i == 7) {
                stat.setName("View Detail Card");
            } else if (i == 8) {
                stat.setName("Download Contact");
            } else if (i == 9) {
                stat.setName("Request to View Card");
            } else {
                stat.setName("View Card");
            }

            data.add(stat);
        }

        return data;
    }

    private List<StatisticPageResponse> convertFrom(List<Object[]> objects) {
        List<StatisticPageResponse> data = new ArrayList<>();

        if (objects != null) {
            for (Object[] obj : objects) {
                StatisticPageResponse stat = new StatisticPageResponse();

                stat.setId(Integer.parseInt(obj[0].toString()));
                stat.setName(obj[1].toString());
                stat.setStats(Integer.parseInt(obj[2].toString()));

                data.add(stat);
            }
        }

        return data;
    }

}
