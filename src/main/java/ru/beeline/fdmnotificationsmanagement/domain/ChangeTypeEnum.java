package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.*;

@Data
@Entity
@Table(name = "change_type_enum", schema = "notification")
public class ChangeTypeEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "name")
    private String  name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "description")
    private String  description;
}