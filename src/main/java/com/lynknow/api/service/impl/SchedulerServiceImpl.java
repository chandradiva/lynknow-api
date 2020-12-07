package com.lynknow.api.service.impl;

import com.lynknow.api.model.UserData;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.SchedulerService;
import com.lynknow.api.service.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserDataRepository userDataRepo;

    @Scheduled(cron = "0 1 0 * * ?") // running on 00:01:00
//    @Scheduled(cron = "*/45 * * * * *") // every 45 seconds - for testing only
    @Override
    public void checkExpiredPremiumUser() {
        try {
            LOGGER.error("Start Scheduler Check Subscription User on: " + new Date());

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);

            today = cal.getTime();
            List<UserData> expiredUsers = userDataRepo.getExpiredUser(today);
            if (expiredUsers != null) {
                for (UserData item : expiredUsers) {
                    userDataService.resetToBasic(item);
                }
            }

            LOGGER.error("End Scheduler Check Subscription User on: " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
        }
    }

    @Scheduled(cron = "0 10 0 * * ?") // running on 00:10:00
//    @Scheduled(cron = "*/45 * * * * *") // every 45 seconds - for testing only
    @Override
    public void checkExpiredTotalViewUser() {
        try {
            LOGGER.error("Start Scheduler Expired Total View User on: " + new Date());

            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);

            today = cal.getTime();
            List<UserData> expiredUsers = userDataRepo.getExpiredUser(today);
            if (expiredUsers != null) {
                for (UserData item : expiredUsers) {
                    userDataService.resetTotalView(item);
                }
            }

            LOGGER.error("End Scheduler Expired Total View User on: " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error processing data", e);
        }
    }

}
