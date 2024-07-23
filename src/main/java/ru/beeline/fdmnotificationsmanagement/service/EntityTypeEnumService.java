package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.exception.BadRequestException;
import ru.beeline.fdmnotificationsmanagement.repository.EntityTypeEnumRepository;

import java.util.Objects;

@Service
public class EntityTypeEnumService {
    private static EntityTypeEnum techCapability = null;
    private static EntityTypeEnum tech = null;
    private static EntityTypeEnum businessCapability = null;

    @Autowired
    private EntityTypeEnumRepository entityTypeEnumRepository;


    public EntityTypeEnum getTechCapabilityEntityTypeEnum() {
        if (Objects.isNull(techCapability)) {
            techCapability = entityTypeEnumRepository.findByType(EntityTypeEnum.CapabilitySubscriptionType.TECH_CAPABILITY);
        }
        return techCapability;
    }

    public EntityTypeEnum getBusinessCapabilityEntityTypeEnum() {
        if (Objects.isNull(businessCapability)) {
            businessCapability = entityTypeEnumRepository.findByType(EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_CAPABILITY);
        }
        return businessCapability;
    }

    public EntityTypeEnum getTechEntityTypeEnum() {
        if (Objects.isNull(tech)) {
            tech = entityTypeEnumRepository.findByType(EntityTypeEnum.CapabilitySubscriptionType.TECH);
        }
        return tech;
    }

    public EntityTypeEnum getEntityTypeEnumByTypeName(String typeName) {
        try {
            EntityTypeEnum.CapabilitySubscriptionType.valueOf(typeName);
        } catch (Exception e) {
            throw new BadRequestException("Неверно указан тип сущности");
        }
        return entityTypeEnumRepository.findByType(EntityTypeEnum.CapabilitySubscriptionType.valueOf(typeName));
    }
}