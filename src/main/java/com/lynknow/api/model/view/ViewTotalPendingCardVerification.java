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
@Table(name = "view_total_pending_card_verification", schema = "public")
public class ViewTotalPendingCardVerification {

    @Id
    @Column(name = "total_pending")
    private Integer totalPending;

    @Column(name = "total_pending_personal_name")
    private Integer totalPendingPersonalName;

    @Column(name = "total_pending_personal_address")
    private Integer totalPendingPersonalAddress;

    @Column(name = "total_pending_designation")
    private Integer totalPendingDesignation;

    @Column(name = "total_pending_company_name")
    private Integer totalPendingCompanyName;

    @Column(name = "total_pending_company_contact")
    private Integer totalPendingCompanyContact;

    @Column(name = "total_pending_company_address")
    private Integer totalPendingCompanyAddress;

}
