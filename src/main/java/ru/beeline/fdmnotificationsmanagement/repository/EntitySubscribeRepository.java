package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntitySubscribe;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;

@Repository
public interface EntitySubscribeRepository extends JpaRepository<EntitySubscribe, Long> {
    Long countByUserIdAndEntityIdAndEntityType(Integer userId, Integer entityId, EntityTypeEnum entityType);
}