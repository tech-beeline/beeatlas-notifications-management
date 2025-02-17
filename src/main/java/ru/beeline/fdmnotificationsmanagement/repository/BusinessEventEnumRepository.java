package ru.beeline.fdmnotificationsmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.fdmnotificationsmanagement.domain.BusinessEventEnum;

public interface BusinessEventEnumRepository extends JpaRepository<BusinessEventEnum, Integer> {
    BusinessEventEnum findByName(String name);
}