package ru.beeline.fdmnotificationsmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

@Data
@javax.persistence.Entity
@Table(name = "entity", schema = "notification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_id_generator")
    @SequenceGenerator(name = "entity_id_generator", sequenceName = "seq_entity_id", allocationSize = 1)
    private Integer id;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "name")
    private String name;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "entity_type_id", referencedColumnName = "id")
    private EntityTypeEnum entityType;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
    private List<EntityChange> entityChange;

    @ToString.Exclude
    @ApiModelProperty(hidden = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
    private List<Subscribe> subscribes;

}