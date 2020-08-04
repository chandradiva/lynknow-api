package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "otp_type", schema = "public")
public class OtpType {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "otp_type_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Integer id;

    @Column(name = "name")
    private String name;

}
