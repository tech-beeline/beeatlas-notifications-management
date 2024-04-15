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
@Table(name = "entity_change_sub", schema = "notification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityChangeSub {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_change_sub_id_generator")
    @SequenceGenerator(name = "entity_change_sub_id_generator", sequenceName = "entity_change_sub_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "id_sub")
    private Integer idSub;

    @ManyToOne
    @JoinColumn(name = "id_entity_change", referencedColumnName = "id")
    private EntityChange entityChange;
}