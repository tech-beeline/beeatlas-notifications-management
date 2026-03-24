/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.beeline.fdmnotificationsmanagement.domain.BusinessNotify;

public interface BusinessNotifyRepository extends JpaRepository<BusinessNotify, Integer>, JpaSpecificationExecutor<BusinessNotify> {
}