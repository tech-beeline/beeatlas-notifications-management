package ru.beeline.fdmnotificationsmanagement.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "entity_change", schema = "notification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityChange {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_change_id_generator")
    @SequenceGenerator(name = "entity_change_id_generator", sequenceName = "seq_entity_change_id", allocationSize = 1)
    private Integer id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "entity_id", referencedColumnName = "id")
    private ru.beeline.fdmnotificationsmanagement.domain.Entity entity;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "children_entity_id", referencedColumnName = "id")
    private ru.beeline.fdmnotificationsmanagement.domain.Entity child;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "date_change")
    private Timestamp dateChange;

    @ToString.Exclude
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "entityChange", cascade = CascadeType.ALL)
    private List<Notify> notifies;
}