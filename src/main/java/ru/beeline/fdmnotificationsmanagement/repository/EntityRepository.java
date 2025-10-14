package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;

import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Integer> {

    Entity findByEntityIdAndEntityType(Integer entityId, EntityTypeEnum entityType);
    Entity findByEntityId(Integer entityId);

    List<Entity> findAllByEntityIdInAndEntityType(List<Integer> entityId, EntityTypeEnum entityType);
}