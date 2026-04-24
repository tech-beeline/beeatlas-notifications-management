/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "entity_type_enum", schema = "notification")
public class EntityTypeEnum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "alias")
    private String  alias;

    @Column(name = "type")
    private String type;

    @Column(name = "base_link_template")
    private String baseLinkTemplate;


    public enum CapabilitySubscriptionType {
        TECH,
        TECH_CAPABILITY,
        BUSINESS,
        BUSINESS_CAPABILITY,
        arch_interface
    }
}