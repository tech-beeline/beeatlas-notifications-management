/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "notify", schema = "notification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notify {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notify_id_generator")
    @SequenceGenerator(name = "notify_id_generator", sequenceName = "seq_notify_id", allocationSize = 1)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")
    private EntityChange entityChange;

    @Column(name = "web_notify")
    private Boolean webNotify;

    @Column(name = "email_notify")
    private Boolean emailNotify;
}