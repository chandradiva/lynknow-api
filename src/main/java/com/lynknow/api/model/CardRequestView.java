package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "card_request_view", schema = "public")
public class CardRequestView {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "card_request_view_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_card_id")
    private UserCard userCard;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @Column(name = "is_granted")
    private int isGranted = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_request_date")
    private Date expiredRequestDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "is_active")
    private int isActive = 1;

}
