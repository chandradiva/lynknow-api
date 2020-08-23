package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "card_verification_credit_usage", schema = "public")
public class CardVerificationCreditUsage {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "card_verification_credit_usage_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_verification_id")
    private CardVerification cardVerification;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

}
