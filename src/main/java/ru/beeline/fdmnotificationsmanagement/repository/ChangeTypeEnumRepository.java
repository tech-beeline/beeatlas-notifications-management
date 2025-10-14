package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.ChangeTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;

@Repository
public interface ChangeTypeEnumRepository extends JpaRepository<ChangeTypeEnum, Long> {
    int countByName(String name);
}