package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;

import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Integer> {
    List<Entity> deleteByIdAndEntityType(Integer id, EntityTypeEnum entityType);

    List<Entity> findAllByEntityIdAndEntityType(Integer entityId, EntityTypeEnum entityType);
}