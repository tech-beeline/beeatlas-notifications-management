package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;
import ru.beeline.fdmnotificationsmanagement.domain.User;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilityParentDTO;
import ru.beeline.fdmnotificationsmanagement.repository.EntityChangeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntityRepository;
import ru.beeline.fdmnotificationsmanagement.repository.NotifyRepository;
import ru.beeline.fdmnotificationsmanagement.repository.SubscribeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.UserRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CapabilitySubscribeService {
    private static String PARENT_ID = "parentId";

    @Value("${integration.gateway-server-url}")
    private String gatewayServerUrl;

    @Value("${integration.frontend-server-url}")
    private String frontendServerUrl;

    @Autowired
    private EntityTypeEnumService entityTypeEnumService;

    @Autowired
    private EntityChangeRepository entityChangeRepository;

    @Autowired
    private NotifyRepository notifyRepository;

//    @Autowired
//    private StatusEnumService statusEnumService;

    @Autowired
    private CapabilityIntegrationService capabilityIntegrationService;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean checkCapabilitySubscribeById(Integer idSubscribe, EntityTypeEnum entityTypeEnum) {
//        return entitySubscribeRepository.countByUserIdAndEntityIdAndEntityType(
//                RequestContext.getUser(),
//                idSubscribe,
//                entityTypeEnum) > 0;
        return true;
    }

    public Boolean checkTechCapabilitySubscribeById(Integer idSubscribe) {
        return checkCapabilitySubscribeById(idSubscribe, entityTypeEnumService.getTechCapabilityEntityTypeEnum());
    }

    public Boolean checkBusinessCapabilitySubscribeById(Integer idSubscribe) {
        return checkCapabilitySubscribeById(idSubscribe, entityTypeEnumService.getBusinessCapabilityEntityTypeEnum());
    }

    public Boolean checkBusinessCapabilityChildrenSubscribeById(String idSubscribe) {
        return false;
//        return subscribeRepository.countByParameterNameAndParameterValueAndUserIdAndEntityTypeName(
//                PARENT_ID,
//                idSubscribe,
//                RequestContext.getUser(),
//                "BUSINESS_CAPABILITY") > 0;
    }


    public void updateSubscribeBusinessCapability(Integer entityId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum);
    }

    public void updateSubscribeTechCapability(Integer entityId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum);
    }

    public void createSubscribeTechCapability(Integer entityId) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getTechCapabilityParents(entityId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        if (capabilityParentDTO != null)
            createSubscribe(entityId, capabilityParentDTO, entityTypeEnum);
    }

    public void createSubscribeBusinessCapability(Integer entityId) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getBusinessCapabilityParents(entityId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        createSubscribe(entityId, capabilityParentDTO, entityTypeEnum);
    }

    private void updateSubscribe(Integer entityId, EntityTypeEnum entityTypeEnum) {
        List<Entity> entities = entityRepository.findAllByEntityIdAndEntityType(entityId, entityTypeEnum);
        if (!entities.isEmpty()) {
            List<Subscribe> subscribes = subscribeRepository.findAllByEntityIn(entities);
            if (!subscribes.isEmpty()) {
                List<EntityChange> entityChanges = subscribes.stream()
                        .map(subscribe -> EntityChange.builder()
                                .entity(subscribe.getEntity())
                                .dateChange(Timestamp.valueOf(LocalDateTime.now()))
                                .changeType("UPDATE")
                                .notifies(List.of(Notify.builder()
                                        .user(subscribe.getUser())
                                        .webNotify(false)
                                        .emailNotify(false)
                                        .build()))
                                .build())
                        .collect(Collectors.toList());

                entityChangeRepository.saveAll(entityChanges);
            }
        }
    }

    private void createSubscribe(Integer entityId,
                                 CapabilityParentDTO capabilityParentDTO,
                                 EntityTypeEnum entityTypeEnum) {
        List<Subscribe> subscribes = subscribeRepository.findByAutoSubChildrenTrue();
        if (!subscribes.isEmpty()) {
            List<Entity> entities = subscribes.stream()
                    .map(Subscribe::getEntity)
                    .filter(entity -> entity.getEntityType().equals(entityTypeEnum))
                    .filter(entity -> capabilityParentDTO.getParents().contains(entityId.longValue()))
                    .toList();
            if (!entities.isEmpty()) {
                Entity entity = entityRepository.save(Entity.builder()
                        .entityId(entityId)
                        .link(frontendServerUrl + "//fdm?id=" + entityId + "&type=TECH")
                        .entityType(entityTypeEnum)
                        .build());
                entityChangeRepository.save(
                        EntityChange.builder()
                                .changeType("CREATE")
                                .entity(entity)
                                .dateChange(Timestamp.valueOf(LocalDateTime.now()))
                                .build());
                Set<User> users = entities.stream()
                        .flatMap(ent -> ent.getSubscribes().stream())
                        .map(Subscribe::getUser)
                        .collect(Collectors.toSet());

                List<Subscribe> newSubscribes = users.stream()
                        .map(user -> Subscribe.builder()
                                .entity(entity)
                                .user(user)
                                .autoSubChildren(false)
                                .build())
                        .collect(Collectors.toList());

                subscribeRepository.saveAll(newSubscribes);

                List<Notify> notifies = users.stream()
                        .map(user -> Notify.builder()
                                .user(user)
                                .webNotify(false)
                                .emailNotify(false)
                                .build())
                        .collect(Collectors.toList());
                notifyRepository.saveAll(notifies);

            }

        }

    }

//    private EntitySubscribe createAndSaveEntitySubscribe(Integer entityId, Integer userId) {
//        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
//        return entitySubscribeRepository.save(
//                EntitySubscribe.builder()
//                        .userId(userId)
//                        .entityId(entityId)
//                        .entityType(entityTypeEnum)
//                        .build());
//    }
//
//    private void createAndSaveEntityChange(ChangeTypeEnum changeType, Integer entityId,
//                                           EntityTypeEnum entityTypeEnum,
//                                           List<EntitySubscribe> entitySubscribes,
//                                           String capabilityType) {
//        EntityChange entityChange = entityChangeRepository.save(
//                EntityChange.builder()
//                        .entityId(entityId)
//                        .link(gatewayServerUrl + "/api/" + capabilityType + "/" + entityId)
//                        .changeType(changeType)
//                        .status(statusEnumService.getWaitNotifyStatusEnum())
//                        .entityType(entityTypeEnum)
//                        .build());
//
//        entitySubscribes.forEach(subscribe ->
//                entityChangeSubRepository.save(
//                        EntityChangeSub.builder()
//                                .idSub(subscribe.getId())
//                                .entityChange(entityChange)
//                                .build()
//                ));
//    }

    public List<Integer> getAllEntitySubscribeByUserIdAndEntityType(Integer userId, String entityType) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            List<Subscribe> subscribes = subscribeRepository.findAllByUser(user);
            if (!subscribes.isEmpty()) {
                EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);
                return subscribes.stream()
                        .map(Subscribe::getEntity)
                        .filter(entity -> entityTypeEnum == entity.getEntityType())
                        .map(Entity::getEntityId)
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public Integer findOrCreateSubscription(EntityTypeEnum.CapabilitySubscriptionType capabilityType, Integer entityId, Integer userId) {
//        EntityTypeEnum entityTypeEnum;
//        if (capabilityType.equals(EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_WITH_CHILDREN)) {
//            entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
//            Entity entity = entityAutoSubscribeRepository.findByUserIdAndEntityType(userId, entityTypeEnum);
//            if (entity != null) {
//                Subscribe subscribe = subscribeRuleRepository.findByParameterNameAndParameterValueAndAutoSubId(
//                        PARENT_ID, String.valueOf(entityId), entity.getId());
//                if (subscribe != null) return subscribe.getId();
//            }
//
//        } else {
//            if (capabilityType.equals(EntityTypeEnum.CapabilitySubscriptionType.TECH)) {
//                entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
//            } else {
//                entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
//            }
//
//            EntitySubscribe entitySubscribe = entitySubscribeRepository.findByUserIdAndEntityIdAndEntityType(userId,
//                    entityId, entityTypeEnum);
//            if (entitySubscribe == null) {
//                EntitySubscribe newEntitySubscribe = EntitySubscribe.builder()
//                        .userId(userId)
//                        .entityId(entityId)
//                        .entityType(entityTypeEnum)
//                        .build();
//                newEntitySubscribe = entitySubscribeRepository.save(newEntitySubscribe);
//                return newEntitySubscribe.getId();
//            } else return entitySubscribe.getId();
//        }
//        return null;
        return null;
    }

    public void deleteSubscribe(Integer entityId, Integer userId, String entityType) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);
            if (entityTypeEnum != null) {
                Entity entity = entityRepository.findByIdAndEntityType(entityId, entityTypeEnum);
                if (entity != null) {
                    subscribeRepository.deleteByUserAndEntity(user, entity);
                    List<EntityChange> entityChanges = entityChangeRepository.findAllByEntityId(entityId);
                    if (!entityChanges.isEmpty()) {
                        notifyRepository.deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(
                                user,
                                false,
                                false,
                                entityChanges);
                    }
                }
            }
        }
    }
}