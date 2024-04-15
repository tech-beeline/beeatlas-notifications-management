package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.controller.RequestContext;
import ru.beeline.fdmnotificationsmanagement.domain.ChangeTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.repository.ChangeTypeEnumRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntityChangeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntitySubscribeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntityTypeEnumRepository;
import ru.beeline.fdmnotificationsmanagement.repository.SubscribeRuleRepository;

import java.util.Objects;

@Service
public class EntityTypeEnumService {
    private static EntityTypeEnum techCapability = null;
    private static EntityTypeEnum businessCapability = null;

    @Autowired
    private EntityTypeEnumRepository entityTypeEnumRepository;


    public EntityTypeEnum getTechCapabilityEntityTypeEnum() {
        if (Objects.isNull(techCapability)) {
            techCapability = entityTypeEnumRepository.findByType("TECH_CAPABILITY");
        }
        return techCapability;
    }

    public EntityTypeEnum getBusinessCapabilityEntityTypeEnum() {
        if (Objects.isNull(businessCapability)) {
            businessCapability = entityTypeEnumRepository.findByType("BUSINESS_CAPABILITY");
        }
        return businessCapability;
    }

}