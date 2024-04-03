package ru.beeline.fdmnotificationsmanagement.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "entity_change_sub", schema = "notification")
public class EntityChangeSub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_sub")
    private Integer idSub;

    @ManyToOne
    @JoinColumn(name = "id_entity_change", referencedColumnName = "id")
    private EntityChange entityChange;
}