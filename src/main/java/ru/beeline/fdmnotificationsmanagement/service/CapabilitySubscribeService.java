package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.controller.RequestContext;
import ru.beeline.fdmnotificationsmanagement.domain.*;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilityParentDTO;
import ru.beeline.fdmnotificationsmanagement.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CapabilitySubscribeService {
    private static String PARENT_ID = "parentId";

    @Value("${integration.gateway-server-url}")
    private String gatewayServerUrl;

    @Autowired
    private EntitySubscribeRepository entitySubscribeRepository;

    @Autowired
    private EntityTypeEnumService entityTypeEnumService;

    @Autowired
    private EntityChangeRepository entityChangeRepository;

    @Autowired
    private ChangeTypeEnumService changeTypeEnumService;

    @Autowired
    private EntityChangeSubRepository entityChangeSubRepository;

    @Autowired
    private StatusEnumService statusEnumService;

    @Autowired
    private CapabilityIntegrationService capabilityIntegrationService;

    @Autowired
    private SubscribeRuleRepository subscribeRuleRepository;

    @Autowired
    private EntityAutoSubscribeRepository entityAutoSubscribeRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean checkCapabilitySubscribeById(Integer idSubscribe, EntityTypeEnum entityTypeEnum) {
        return entitySubscribeRepository.countByUserIdAndEntityIdAndEntityType(
                RequestContext.getUser(),
                idSubscribe,
                entityTypeEnum) > 0;
    }

    public Boolean checkTechCapabilitySubscribeById(Integer idSubscribe) {
        return checkCapabilitySubscribeById(idSubscribe, entityTypeEnumService.getTechCapabilityEntityTypeEnum());
    }

    public Boolean checkBusinessCapabilitySubscribeById(Integer idSubscribe) {
        return checkCapabilitySubscribeById(idSubscribe, entityTypeEnumService.getBusinessCapabilityEntityTypeEnum());
    }

    public Boolean checkBusinessCapabilityChildrenSubscribeById(String idSubscribe) {
        return subscribeRuleRepository.countByParameterNameAndParameterValueAndUserIdAndEntityTypeName(
                PARENT_ID,
                idSubscribe,
                RequestContext.getUser(),
                "BUSINESS_CAPABILITY") > 0;
    }


    public void updateSubscribeBusinessCapability(Integer entityId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, "business-capability");
    }

    public void updateSubscribeTechCapability(Integer entityId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, "tech-capability");
    }

    public void createSubscribeTechCapability(Integer entityId) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getTechCapabilityParents(entityId);
        createSubscribe(entityId, "TECH_CAPABILITY", "tech-capability", capabilityParentDTO);
    }

    public void createSubscribeBusinessCapability(Integer entityId) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getBusinessCapabilityParents(entityId);
        createSubscribe(entityId, "BUSINESS_CAPABILITY", "business-capability", capabilityParentDTO);
    }

    private void updateSubscribe(Integer entityId, EntityTypeEnum entityTypeEnum, String capabilityType) {
        List<EntitySubscribe> entitySubscribe = entitySubscribeRepository.findAllByEntityIdAndEntityType(entityId, entityTypeEnum);
        if (!entitySubscribe.isEmpty()) {
            createAndSaveEntityChange(changeTypeEnumService.getUpdateChangeTypeEnum(), entityId, entityTypeEnum, entitySubscribe, capabilityType);
        }
    }

    private void createSubscribe(Integer entityId, String capabilityTypeName, String capabilityType, CapabilityParentDTO capabilityParentDTO) {
        if (capabilityParentDTO == null) {
            return;
        }

        List<Integer> entityAutoSubscribes = capabilityParentDTO.getParents().stream()
                .flatMap(parent -> subscribeRuleRepository.getByParameterNameAndParameterValueAndEntityTypeName(
                                PARENT_ID,
                                parent.toString(),
                                capabilityTypeName).stream()
                        .map(SubscribeRule::getAutoSubId))
                .collect(Collectors.toList());

        List<EntityAutoSubscribe> autoSubscribes = entityAutoSubscribeRepository.findAllByIdIn(entityAutoSubscribes);
        if (autoSubscribes.isEmpty()) {
            return;
        }

        List<EntitySubscribe> entitySubscribes = autoSubscribes.stream()
                .map(autoSubscribe -> createAndSaveEntitySubscribe(entityId, autoSubscribe.getUserId()))
                .collect(Collectors.toList());

        EntityTypeEnum entityType;
        if(capabilityTypeName.equals("BUSINESS_CAPABILITY")){
            entityType = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        } else {
            entityType = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        }

        createAndSaveEntityChange(changeTypeEnumService.getCreateChangeTypeEnum(), entityId,
                entityType,
                entitySubscribes,
                capabilityType);
    }

    private EntitySubscribe createAndSaveEntitySubscribe(Integer entityId, Integer userId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        return entitySubscribeRepository.save(
                EntitySubscribe.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .entityType(entityTypeEnum)
                        .build());
    }

    private void createAndSaveEntityChange(ChangeTypeEnum changeType, Integer entityId,
                                           EntityTypeEnum entityTypeEnum,
                                           List<EntitySubscribe> entitySubscribes,
                                           String capabilityType) {
        EntityChange entityChange = entityChangeRepository.save(
                EntityChange.builder()
                        .entityId(entityId)
                        .link(gatewayServerUrl + "/api/" + capabilityType + "/" + entityId)
                        .changeType(changeType)
                        .status(statusEnumService.getWaitNotifyStatusEnum())
                        .entityType(entityTypeEnum)
                        .build());

        entitySubscribes.forEach(subscribe ->
                entityChangeSubRepository.save(
                        EntityChangeSub.builder()
                                .idSub(subscribe.getId())
                                .entityChange(entityChange)
                                .build()
                ));
    }

    public List<Integer> getAllEntitySubscribeByUserId(Integer userId, String entityType) {
        try {
            List<EntitySubscribe> entitySubscribes = entitySubscribeRepository.findAllByUserIdAndEntityType(userId, entityTypeEnumService.getEntityTypeEnumByTypeName(entityType));
            return entitySubscribes.stream().map(EntitySubscribe::getEntityId).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Integer findOrCreateSubscription(EntityTypeEnum.CapabilitySubscriptionType capabilityType, Integer entityId, Integer userId) {
        EntityTypeEnum entityTypeEnum;
        if (capabilityType.equals(EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_WITH_CHILDREN)) {
            entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
            EntityAutoSubscribe entityAutoSubscribe = entityAutoSubscribeRepository.findByUserIdAndEntityType(userId, entityTypeEnum);
            if (entityAutoSubscribe != null) {
                SubscribeRule subscribeRule = subscribeRuleRepository.findByParameterNameAndParameterValueAndAutoSubId(
                        PARENT_ID, String.valueOf(entityId), entityAutoSubscribe.getId());
                if (subscribeRule != null) return subscribeRule.getId();
            }

        } else {
            if (capabilityType.equals(EntityTypeEnum.CapabilitySubscriptionType.TECH)) {
                entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
            } else {
                entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
            }

            EntitySubscribe entitySubscribe = entitySubscribeRepository.findByUserIdAndEntityIdAndEntityType(userId,
                    entityId, entityTypeEnum);
            if (entitySubscribe == null) {
                EntitySubscribe newEntitySubscribe = EntitySubscribe.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .entityType(entityTypeEnum)
                        .build();
                newEntitySubscribe = entitySubscribeRepository.save(newEntitySubscribe);
                return newEntitySubscribe.getId();
            } else return entitySubscribe.getId();
        }
        return null;
    }

    public void deleteSubscription(Integer entityId, Integer userId, String entityType) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);

        if (entityTypeEnum == null) {
            return;
        }

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            return;
        }

        EntitySubscribe entitySubscribe = entitySubscribeRepository.findByUserIdAndEntityIdAndEntityType(user.getId(), entityId, entityTypeEnum);

        if (entitySubscribe == null) {
            return;
        }

        entityChangeSubRepository.deleteByIdSub(entitySubscribe.getId());
        entitySubscribeRepository.deleteById(entitySubscribe.getId());

    }

}