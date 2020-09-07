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
@Table(name = "view_total_pending_personal_verification", schema = "public")
public class ViewTotalPendingPersonalVerification {

    @Id
    @Column(name = "total_pending")
    private Integer totalPending;

    @Column(name = "total_pending_gov_id")
    private Integer totalPendingGovId;

    @Column(name = "total_pending_employee_doc")
    private Integer totalPendingEmployeeDoc;

}
