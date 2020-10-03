package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "card_verification", schema = "public")
public class CardVerification {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "card_verification_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_card_id")
    private UserCard userCard;

    @ManyToOne
    @JoinColumn(name = "card_verification_item_id")
    private CardVerificationItem cardVerificationItem;

    @Column(name = "is_verified")
    private int isVerified = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "param")
    private String param;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_date")
    private Date expiredDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "is_requested")
    private int isRequested = 0;

    @ManyToOne
    @JoinColumn(name = "verified_by_id")
    private UserData verifiedBy;

    @Column(name = "verified_date")
    private Date verifiedDate;

    @Column(name = "is_otp_generated")
    private int isOtpGenerated = 0;

}
