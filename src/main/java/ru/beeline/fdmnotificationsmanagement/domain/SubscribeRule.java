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
@Table(name = "subscribe_rule", schema = "notification")
public class SubscribeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "auto_sub_id")
    private Integer autoSubId;

    @Column(name = "parameter_name")
    private String parameterName;

    @Column(name = "parameter_value")
    private String parameterValue;
}