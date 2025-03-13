package ru.beeline.fdmnotificationsmanagement.domain.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.beeline.fdmnotificationsmanagement.domain.BusinessEventEnum;
import ru.beeline.fdmnotificationsmanagement.domain.BusinessNotify;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;

public class BusinessNotifySpecifications {

    public static Specification<BusinessNotify> hasUserId(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<BusinessNotify> hasWebNotify(Boolean webNotify) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("webNotify"), webNotify);
    }

    public static Specification<BusinessNotify> hasChangeDateAfter(LocalDateTime changeDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), changeDate);
    }

    public static Specification<BusinessNotify> hasChangeDateBefore(LocalDateTime changeDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), changeDate);
    }

    public static Specification<BusinessNotify> hasEntityType(String type) {
        return (root, query, criteriaBuilder) -> {
            Join<BusinessNotify, BusinessEventEnum> entityTypeJoin = root.join("entityType", JoinType.INNER);
            return criteriaBuilder.equal(entityTypeJoin.get("name"), type);
        };
    }
}
