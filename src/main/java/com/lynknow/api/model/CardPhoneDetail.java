package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "card_phone_detail", schema = "public")
public class CardPhoneDetail {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "card_phone_detail_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_card_id")
    private UserCard userCard;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "dial_code")
    private String dialCode;

    @Column(name = "number")
    private String number;

    @Column(name = "type")
    private int type = 1;

}
