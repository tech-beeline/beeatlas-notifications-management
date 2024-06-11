package ru.beeline.fdmnotificationsmanagement.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "\"user\"", schema = "notification")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email")
    private String email;

    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notify> notifies;

    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscribe> subscribes;
}