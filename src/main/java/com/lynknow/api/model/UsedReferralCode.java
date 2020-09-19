package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "used_referral_code", schema = "public")
public class UsedReferralCode {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "used_referral_code_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "referral_user_card_id")
    private UserCard referralUserCard;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "additional_view")
    private int additionalView = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "is_active")
    private int isActive = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserCard getReferralUserCard() {
        return referralUserCard;
    }

    public void setReferralUserCard(UserCard referralUserCard) {
        this.referralUserCard = referralUserCard;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public int getAdditionalView() {
        return additionalView;
    }

    public void setAdditionalView(int additionalView) {
        this.additionalView = additionalView;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
