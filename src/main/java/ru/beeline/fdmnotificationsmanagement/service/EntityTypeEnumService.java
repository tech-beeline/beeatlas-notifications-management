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

    public EntityTypeEnum getTechEntityTypeEnum() {
        if (Objects.isNull(tech)) {
            tech = entityTypeEnumRepository.findByType("TECH");
        }
        return tech;
    }

    public EntityTypeEnum getEntityTypeEnumByTypeName(String typeName) {
            EntityTypeEnum entityTypeEnum = entityTypeEnumRepository.findByType(typeName);
            if (entityTypeEnum == null) {
                throw new BadRequestException("Неверно указан тип сущности");
            }
        return entityTypeEnumRepository.findByType(typeName);
    }
}