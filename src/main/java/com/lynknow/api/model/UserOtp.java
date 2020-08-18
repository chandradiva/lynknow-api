package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_otp", schema = "public")
public class UserOtp {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "user_otp_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "user_card_id")
    private UserCard userCard;

    @ManyToOne
    @JoinColumn(name = "otp_type_id")
    private OtpType otpType;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "send_to")
    private String sendTo;

    @Column(name = "expired_date")
    private Date expiredDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "is_active")
    private int isActive = 0;

}
