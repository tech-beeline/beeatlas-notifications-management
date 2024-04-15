package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "entity_subscribe", schema = "notification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntitySubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_subscribe_id_generator")
    @SequenceGenerator(name = "entity_subscribe_id_generator", sequenceName = "entity_subscribe_sub_id_seq", allocationSize = 1)

    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "entity_id")
    private Integer entityId;

    @ManyToOne
    @JoinColumn(name = "entity_type_id", referencedColumnName = "id")
    private EntityTypeEnum entityType;
}