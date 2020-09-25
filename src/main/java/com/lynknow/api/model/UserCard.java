package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_card", schema = "public")
public class UserCard {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "user_card_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @Column(name = "front_side")
    private String frontSide;

    @Column(name = "back_side")
    private String backSide;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "company")
    private String company;

    @Column(name = "address_1")
    private String address1;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "whatsapp_no")
    private String whatsappNo;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "fb_email")
    private String fbEmail;

    @Column(name = "google_email")
    private String googleEmail;

    @Column(name = "is_published")
    private int isPublished = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "published_date")
    private Date publishedDate;

    @Column(name = "unique_code")
    private String uniqueCode;

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

    @Column(name = "is_card_locked")
    private int isCardLocked = 0;

    @Column(name = "verification_point")
    private int verificationPoint = 0;

    @Column(name = "is_whatsapp_no_verified")
    private int isWhatsappNoVerified = 0;

    @Column(name = "is_email_verified")
    private int isEmailVerified = 0;

    @Column(name = "other_mobile_no")
    private String otherMobileNo;

}
