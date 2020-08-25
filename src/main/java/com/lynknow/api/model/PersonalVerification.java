package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "personal_verification", schema = "public")
public class PersonalVerification {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "personal_verification_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "personal_verification_item_id")
    private PersonalVerificationItem personalVerificationItem;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "param")
    private String param;

    @Column(name = "is_verified")
    private int isVerified = 0;

    @Column(name = "is_requested")
    private int isRequested = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_date")
    private Date expiredDate;

    @ManyToOne
    @JoinColumn(name = "verified_by_id")
    private UserData verifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "verified_date")
    private Date verifiedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "reason")
    private String reason;

}
