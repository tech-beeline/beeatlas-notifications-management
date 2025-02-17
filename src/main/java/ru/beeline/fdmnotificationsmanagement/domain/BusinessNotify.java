package ru.beeline.fdmnotificationsmanagement.domain;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "business_notify", schema = "notification")
@AllArgsConstructor
@NoArgsConstructor
public class BusinessNotify {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "business_notify_id_generator")
    @SequenceGenerator(name = "business_notify_id_generator", sequenceName = "seq_business_notify_id", allocationSize = 1, schema = "notification")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "entity_id")
    private Integer entityId;

    @ManyToOne
    @JoinColumn(name = "entity_type_id")
    private BusinessEventEnum entityType;

    @Column(name = "web_notify")
    private Boolean webNotify;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}