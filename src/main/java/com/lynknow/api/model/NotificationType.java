package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "notification_type", schema = "public")
public class NotificationType {

    @Id
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name = "name")
    private String name;

}
