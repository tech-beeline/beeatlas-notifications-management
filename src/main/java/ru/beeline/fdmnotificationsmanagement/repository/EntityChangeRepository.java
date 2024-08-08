package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;

import java.util.List;

@Repository
public interface EntityChangeRepository extends JpaRepository<EntityChange, Long> {
    List<EntityChange> findAllByEntity(Entity entity);
}