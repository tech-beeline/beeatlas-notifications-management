/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.domain.specification;


import org.springframework.data.jpa.domain.Specification;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.sql.Timestamp;
import java.time.LocalTime;

public class NotifySpecifications {
    public static Specification<Notify> hasUserId(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), userId);
    }

    public static Specification<Notify> hasWebNotify(Boolean webNotify) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("webNotify"), webNotify);
    }

    public static Specification<Notify> hasChangeDateAfter(Timestamp changeDate) {
        return (root, query, criteriaBuilder) -> {
            Join<Notify, EntityChange> changeEntityJoin = root.join("entityChange", JoinType.INNER);
            return criteriaBuilder.greaterThanOrEqualTo(changeEntityJoin.get("dateChange"), changeDate);
        };
    }

    public static Specification<Notify> hasChangeDateBefore(Timestamp changeDate) {
        return (root, query, criteriaBuilder) -> {
            Join<Notify, EntityChange> changeEntityJoin = root.join("entityChange", JoinType.INNER);
            Timestamp adjustedChangeDate = Timestamp.valueOf(changeDate.toLocalDateTime().with(LocalTime.MAX));
            return criteriaBuilder.lessThanOrEqualTo(changeEntityJoin.get("dateChange"), adjustedChangeDate);
        };
    }

    public static Specification<Notify> hasEntityType(String type) {
        return (root, query, criteriaBuilder) -> {
            Join<Notify, EntityChange> changeEntityJoin = root.join("entityChange", JoinType.INNER);
            Join<EntityChange, Entity> entityJoin = changeEntityJoin.join("entity", JoinType.INNER);
            Join<Entity, EntityTypeEnum> entityTypeEnumJoin = entityJoin.join("entityType", JoinType.INNER);
            return criteriaBuilder.equal(entityTypeEnumJoin.get("type"), type);
        };
    }
}