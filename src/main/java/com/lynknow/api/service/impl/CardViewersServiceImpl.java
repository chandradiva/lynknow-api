package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.model.*;
import com.lynknow.api.pojo.PaginationModel;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.pojo.response.UserDataResponse;
import com.lynknow.api.repository.CardViewersRepository;
import com.lynknow.api.repository.NotificationRepository;
import com.lynknow.api.repository.NotificationTypeRepository;
import com.lynknow.api.service.CardViewersService;
import com.lynknow.api.util.EmailUtil;
import com.lynknow.api.util.GenerateResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CardViewersServiceImpl implements CardViewersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardViewersServiceImpl.class);

    @Autowired
    private CardViewersRepository cardViewersRepo;

    @Autowired
    private GenerateResponseUtil generateRes;

    @Autowired
    private NotificationTypeRepository notificationTypeRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private EmailUtil emailUtil;

    @Value("${fe.url.view-user-card}")
    private String viewUserCardUrl;

    @Override
    public void saveData(UserCard cardSeen, UserData userSeenBy) {
        try {
            CardViewers viewers;
            Page<CardViewers> pageViewers = cardViewersRepo.getByCardSeen(
                    cardSeen.getId(),
                    userSeenBy.getId(),
                    PageRequest.of(0, 1, Sort.by("id").descending()));
            if (pageViewers.getContent() != null && pageViewers.getContent().size() > 0) {
                viewers = pageViewers.getContent().get(0);

                viewers.setUpdatedDate(new Date());
            } else {
                viewers = new CardViewers();

                viewers.setCardSeen(cardSeen);
                viewers.setUserSeenBy(userSeenBy);
                viewers.setCreatedDate(new Date());
                viewers.setIsActive(1);
            }

            cardViewersRepo.save(viewers);
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
        }
    }

    @Override
    public ResponseEntity getListPagination(Long userId, PaginationModel model) {
        try {
            Page<UserDataResponse> page;
            if (model.getSort().equals("asc")) {
                page = cardViewersRepo.getListViewers(
                        userId,
                        PageRequest.of(model.getPage(), Integer.MAX_VALUE, Sort.by("userSeenBy.firstName").ascending())
                ).map(cardViewers -> generateRes.generateResponseUser(cardViewers.getUserSeenBy()));
            } else {
                page = cardViewersRepo.getListViewers(
                        userId,
                        PageRequest.of(model.getPage(), Integer.MAX_VALUE, Sort.by("userSeenBy.firstName").descending())
                ).map(cardViewers -> generateRes.generateResponseUser(cardViewers.getUserSeenBy()));
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
    public void notifyUpdatedCard(UserCard updatedCard, List<Long> userIds) {
        try {
            List<CardViewers> viewers = cardViewersRepo.getListViewers(updatedCard.getId(), userIds);
            if (viewers != null) {
                for (CardViewers item : viewers) {
                    // save notification data
                    NotificationType type = notificationTypeRepo.getDetail(11); // update card info
                    Notification notification = new Notification();

                    notification.setUserData(updatedCard.getUserData());
                    notification.setTargetUserData(item.getUserSeenBy());
                    notification.setTargetUserCard(null);
                    notification.setNotificationType(type);
                    notification.setIsRead(0);
                    notification.setCreatedDate(new Date());
                    notification.setIsActive(1);

                    notificationRepo.save(notification);
                    // end of save notification data

                    // send email
                    String url = viewUserCardUrl + updatedCard.getUniqueCode();
                    emailUtil.sendEmailNoThread(
                            item.getUserSeenBy().getEmail(),
                            "Lynknow - " + updatedCard.getUserData().getFirstName() + " has Updated the Card",
                            "Please click url below to View the Updated Card: <br/><br/> <b><a href=\"" + url + "\">View Card</a></b>");
                    // end of send email
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
        }
    }

}
