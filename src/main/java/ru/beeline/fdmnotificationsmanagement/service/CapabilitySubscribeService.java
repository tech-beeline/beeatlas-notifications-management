package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.controller.RequestContext;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.repository.EntitySubscribeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntityTypeEnumRepository;
import ru.beeline.fdmnotificationsmanagement.repository.SubscribeRuleRepository;

import java.util.Objects;

@Service
public class CapabilitySubscribeService {
    private static String PARENT_ID = "parentId";
    private static String TECH_CAPABILITY = "TECH_CAPABILITY";
    private static String BUSINESS_CAPABILITY = "BUSINESS_CAPABILITY";
    private static EntityTypeEnum techCapability = null;
    private static EntityTypeEnum businessCapability = null;

    @Autowired
    private EntitySubscribeRepository entitySubscribeRepository;

    @Autowired
    private EntityTypeEnumRepository entityTypeEnumRepository;

    @Autowired
    private SubscribeRuleRepository subscribeRuleRepository;

    public Boolean checkTechCapabilitySubscribeById(Integer idSubscribe) {
        return entitySubscribeRepository.countByUserIdAndEntityIdAndEntityType(
                RequestContext.getUser(),
                idSubscribe,
                getTechCapabilityId()) > 0;
    }

    public Boolean checkBusinessCapabilitySubscribeById(Integer idSubscribe) {
        return entitySubscribeRepository.countByUserIdAndEntityIdAndEntityType(
                RequestContext.getUser(),
                idSubscribe,
                getBusinessCapabilityId()) > 0;
    }

    public Boolean checkBusinessCapabilityChildrenSubscribeById(String idSubscribe) {
        return subscribeRuleRepository.contByParameterNameAndParameterValueAndUserIdAndEntityTypeName(
                PARENT_ID,
                idSubscribe,
                RequestContext.getUser(),
                BUSINESS_CAPABILITY) > 0;
    }

    private EntityTypeEnum getTechCapabilityId() {
        if (Objects.isNull(techCapability)) {
            techCapability = entityTypeEnumRepository.findByType(TECH_CAPABILITY);
        }
        return techCapability;
    }

    private EntityTypeEnum getBusinessCapabilityId() {
        if (Objects.isNull(businessCapability)) {
            businessCapability = entityTypeEnumRepository.findByType(BUSINESS_CAPABILITY);
        }
        return businessCapability;
    }
}