/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "entity_type_template_link", schema = "notification")
public class EntityTypeTemplateLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_type_template_link_id_generator")
    @SequenceGenerator(name = "entity_type_template_link_id_generator", sequenceName = "entity_type_template_link_id", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "link_template")
    private String linkTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_type_id")
    private EntityTypeEnum entityType;
}

