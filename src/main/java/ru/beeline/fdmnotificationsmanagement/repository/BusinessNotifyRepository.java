package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.fdmnotificationsmanagement.domain.BusinessNotify;

public interface BusinessNotifyRepository extends JpaRepository<BusinessNotify, Integer> {
}