package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Column(name = "type")
    private String type;


    public enum CapabilitySubscriptionType {
        TECH,
        BUSINESS,
        BUSINESS_WITH_CHILDREN
    }
}