package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_contact", schema = "public")
public class UserContact {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "user_contact_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "from_card_id")
    private UserCard fromCard;

    @ManyToOne
    @JoinColumn(name = "exchange_card_id")
    private UserCard exchangeCard;

    @ManyToOne
    @JoinColumn(name = "exchange_user_id")
    private UserData exchangeUser;

    @Column(name = "status")
    private int status = 0;

    @Column(name = "flag")
    private int flag = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

}
