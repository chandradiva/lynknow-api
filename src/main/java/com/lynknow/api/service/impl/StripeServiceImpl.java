package com.lynknow.api.service.impl;

import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.exception.UnprocessableEntityException;
import com.lynknow.api.model.PurchaseHistory;
import com.lynknow.api.model.SubscriptionPackage;
import com.lynknow.api.model.UserData;
import com.lynknow.api.pojo.request.StripeChargeRequest;
import com.lynknow.api.pojo.response.BaseResponse;
import com.lynknow.api.repository.PurchaseHistoryRepository;
import com.lynknow.api.repository.SubscriptionPackageRepository;
import com.lynknow.api.repository.UserDataRepository;
import com.lynknow.api.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepo;

    @Autowired
    private UserDataRepository userDataRepo;

    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepo;

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${verification.credit.default}")
    private String defaultVerificationCredit;

    @Value("${verification.credit.additional}")
    private String additionalVerificationCredit;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public ResponseEntity createStripeCharge(StripeChargeRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserData userSession = (UserData) auth.getPrincipal();
            UserData userLogin = userDataRepo.getDetail(userSession.getId());

            if (request.getPackageId() < 2 || request.getPackageId() > 5) {
                LOGGER.error("Package not Exist !");
                throw new UnprocessableEntityException("Package not Exist !");
            }

            Map<String, Object> chargeParams = new HashMap<>();

            chargeParams.put("amount", request.getChargeAmount());
            chargeParams.put("currency", request.getCurrency());
            chargeParams.put("description", request.getChargeDesc());
            chargeParams.put("source", request.getChargeToken());

            Charge charge = Charge.create(chargeParams);

            SubscriptionPackage subscriptionPackage = subscriptionPackageRepo.getDetail(request.getPackageId());
            if (request.getPackageId() == 2) {
                // buy premium monthly
                this.setToPremium(userLogin, subscriptionPackage, 1);
            } else if (request.getPackageId() == 3) {
                // buy premium yearly
                this.setToPremium(userLogin, subscriptionPackage, 2);
            } else if (request.getPackageId() == 4) {
                // additional view
                this.addTotalView(userLogin);
            } else if (request.getPackageId() == 5) {
                // additional verification credit
                if (userLogin.getCurrentSubscriptionPackage().getId() == 1) {
                    LOGGER.error("Only Premium Users that Can Buy Verification Credit");
                    throw new UnprocessableEntityException("Only Premium Users that Can Buy Verification Credit");
                }

                this.addVerificationCredit(userLogin);
            } else {
                LOGGER.error("Package not Exist !");
                throw new UnprocessableEntityException("Package not Exist !");
            }

            PurchaseHistory purchase = new PurchaseHistory();

            purchase.setUserData(userLogin);
            purchase.setSubscriptionPackage(subscriptionPackage);
            purchase.setAmount(request.getChargeAmount());
            purchase.setCurrency(request.getCurrency());
            purchase.setSource(request.getChargeToken());
            purchase.setCreatedDate(new Date());
            if (charge != null) {
                purchase.setChargeId(charge.getId());
            }

            purchaseHistoryRepo.save(purchase);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    charge.getId()), HttpStatus.OK);
        } catch (InternalServerErrorException | StripeException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity retrieveCharge(String chargeId) {
        try {
            Charge charge = Charge.retrieve(chargeId);

            return new ResponseEntity(new BaseResponse<>(
                    true,
                    200,
                    "Success",
                    charge.getId()), HttpStatus.OK);
        } catch (InternalServerErrorException | StripeException e) {
            LOGGER.error("Error processing data", e);
            throw new InternalServerErrorException("Error processing data: " + e.getMessage());
        }
    }

    private void setToPremium(UserData user, SubscriptionPackage subs, int type) {
        try {
            Calendar cal = Calendar.getInstance();
            if (user.getExpiredPremiumDate() != null) {
                cal.setTime(user.getExpiredPremiumDate());
            }

            Calendar calView = Calendar.getInstance();
            if (user.getExpiredTotalView() != null) {
                calView.setTime(user.getExpiredTotalView());
            }

            if (type == 1) {
                // monthly
                cal.add(Calendar.MONTH, 1);
                calView.add(Calendar.MONTH, 1);

                user.setMaxTotalView(user.getMaxTotalView() + 500);
                user.setMaxVerificationCredit(user.getMaxVerificationCredit() + 2);
            } else {
                cal.add(Calendar.YEAR, 1);
                calView.add(Calendar.YEAR, 1);

                user.setMaxTotalView(user.getMaxTotalView() + 10000);
                user.setMaxVerificationCredit(user.getMaxVerificationCredit() + 30);
            }

            user.setCurrentSubscriptionPackage(subs);
            user.setExpiredPremiumDate(cal.getTime());
            user.setExpiredTotalView(calView.getTime());
            user.setUpdatedDate(new Date());

            userDataRepo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTotalView(UserData user) {
        try {
            Calendar cal = Calendar.getInstance();
            if (user.getExpiredTotalView() != null) {
                cal.setTime(user.getExpiredTotalView());
            }

            cal.add(Calendar.YEAR, 1);

            if (user.getCurrentSubscriptionPackage().getId() == 1) {
                // basic
                user.setMaxTotalView(user.getMaxTotalView() + 5000);
            } else {
                // pro
                user.setMaxTotalView(user.getMaxTotalView() + 10000);
            }

            user.setExpiredTotalView(cal.getTime());
            user.setUpdatedDate(new Date());

            userDataRepo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVerificationCredit(UserData user) {
        try {
            user.setMaxVerificationCredit(user.getMaxVerificationCredit() + Integer.parseInt(additionalVerificationCredit));
            user.setUpdatedDate(new Date());

            userDataRepo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
