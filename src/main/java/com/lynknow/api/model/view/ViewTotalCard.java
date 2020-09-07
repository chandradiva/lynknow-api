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
@Table(name = "view_total_card", schema = "public")
public class ViewTotalCard {

    @Id
    @Column(name = "total_card")
    private Integer totalCard;

    @Column(name = "total_personal_card")
    private Integer totalPersonalCard;

    @Column(name = "total_company_card")
    private Integer totalCompanyCard;

    @Column(name = "total_employee_card")
    private Integer totalEmployeeCard;

}
