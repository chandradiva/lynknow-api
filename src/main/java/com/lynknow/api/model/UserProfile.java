package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_profile", schema = "public")
public class UserProfile {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "user_profile_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address_1")
    private String address1;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "country")
    private String country;

    @Column(name = "whatsapp_no")
    private String whatsappNo;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "fb_id")
    private String fbId;

    @Column(name = "fb_token")
    private String fbToken;

    @Column(name = "fb_email")
    private String fbEmail;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "google_token")
    private String googleToken;

    @Column(name = "google_email")
    private String googleEmail;

    @Column(name = "is_whatsapp_no_verified")
    private int isWhatsappNoVerified = 0;

    @Column(name = "is_email_verified")
    private int isEmailVerified = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_date")
    private Date deletedDate;

    @Column(name = "is_active")
    private int isActive = 1;

}
