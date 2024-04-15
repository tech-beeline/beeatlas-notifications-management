package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.StatusEnum;

@Repository
public interface StatusEnumRepository extends JpaRepository<StatusEnum, Long> {
    StatusEnum findByStatus(String status);
}