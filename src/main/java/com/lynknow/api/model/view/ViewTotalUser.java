package com.lynknow.api.model.view;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "view_total_user", schema = "public")
public class ViewTotalUser {

    @Id
    @Column(name = "total_user")
    private Integer totalUser;

    @Column(name = "total_basic_user")
    private Integer totalBasicUser;

    @Column(name = "total_premium_user")
    private Integer totalPremiumUser;

}
