package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;

@Repository
public interface EntityTypeEnumRepository extends JpaRepository<EntityTypeEnum, Long> {
    EntityTypeEnum findByType(String type);
}