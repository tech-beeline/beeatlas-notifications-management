package ru.beeline.fdmnotificationsmanagement.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@javax.persistence.Entity
@Table(name = "entity", schema = "notification")
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "entity_type_id", referencedColumnName = "id")
    private EntityTypeEnum entityType;

    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
    private List<EntityChange> entityChange;

    @ApiModelProperty(hidden = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
    private List<Subscribe> subscribes;

}