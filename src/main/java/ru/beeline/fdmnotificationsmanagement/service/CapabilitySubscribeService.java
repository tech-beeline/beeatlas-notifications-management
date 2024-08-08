package ru.beeline.fdmnotificationsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.beeline.fdmlib.dto.capability.BusinessCapabilityChildrenDTO;
import ru.beeline.fdmlib.dto.capability.BusinessCapabilityDTO;
import ru.beeline.fdmlib.dto.capability.TechCapabilityShortDTO;
import ru.beeline.fdmnotificationsmanagement.client.CapabilityClient;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;
import ru.beeline.fdmnotificationsmanagement.domain.User;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilityParentDTO;
import ru.beeline.fdmnotificationsmanagement.repository.SubscribeRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CapabilitySubscribeService {

    @Value("${integration.frontend-server-url}")
    private String frontendServerUrl;

    @Autowired
    private EntityTypeEnumService entityTypeEnumService;

    @Autowired
    private EntityChangeService entityChangeService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private CapabilityClient capabilityClient;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public void updateSubscribeBusinessCapability(Integer entityId, String name) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, name);
    }

    public void updateSubscribeTechCapability(Integer entityId, String entityName) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, entityName);
    }

    public void createSubscribeTechCapability(Integer entityId, String entityName) {
        CapabilityParentDTO capabilityParentDTO = capabilityClient.getTechCapabilityParents(entityId);
        log.info("capabilityParentIDs: " + capabilityParentDTO.getParents().toString());
        if (capabilityParentDTO != null) {
            createSubscribe(entityId,
                    capabilityParentDTO,
                    entityTypeEnumService.getBusinessCapabilityEntityTypeEnum(),
                    entityTypeEnumService.getTechCapabilityEntityTypeEnum(),
                    entityName);
        }
    }

    public void createSubscribeBusinessCapability(Integer entityId, String entityName) {
        CapabilityParentDTO capabilityParentDTO = capabilityClient.getBusinessCapabilityParents(entityId);
        log.info("capabilityParentIDs: " + capabilityParentDTO.getParents().toString());
        if (capabilityParentDTO != null) {
            createSubscribe(entityId,
                    capabilityParentDTO,
                    entityTypeEnumService.getBusinessCapabilityEntityTypeEnum(),
                    entityTypeEnumService.getBusinessCapabilityEntityTypeEnum(),
                    entityName);
        }
    }

    private void updateSubscribe(Integer entityId, EntityTypeEnum entityTypeEnum, String entityName) {
        Entity entity = entityService.findByEntityIdAndEntityType(entityId, entityTypeEnum);
        if (entity != null) {
            log.info("entityID: " + entity.getId());
            if (!entityName.equals(entity.getName())) {
                entity.setName(entityName);
                entityService.save(entity);
            }
            List<Subscribe> subscribes = subscribeRepository.findAllByEntity(entity);
            if (!subscribes.isEmpty()) {
                subscribes.forEach(subscribe -> {
                    EntityChange entityChange = EntityChange.builder()
                            .entity(subscribe.getEntity())
                            .dateChange(Timestamp.valueOf(LocalDateTime.now()))
                            .changeType("UPDATE")
                            .build();
                    entityChange = entityChangeService.save(entityChange);
                    Notify notify = Notify.builder()
                            .user(subscribe.getUser())
                            .webNotify(false)
                            .emailNotify(false)
                            .entityChange(entityChange)
                            .build();
                    notifyService.save(notify);
                });
            }
        }
    }

    private void createSubscribe(Integer entityId,
                                 CapabilityParentDTO capabilityParentDTO,
                                 EntityTypeEnum entityTypeEnumForFind,
                                 EntityTypeEnum entityTypeEnumForCreate,
                                 String entityName) {
        List<Subscribe> subscribes = subscribeRepository.findByAutoSubChildrenTrue();
        log.info("subscribes: " + subscribes.stream().map(Subscribe::getId).collect(Collectors.toList()).toString());
        if (!subscribes.isEmpty()) {
            List<Entity> entities = subscribes.stream()
                    .map(Subscribe::getEntity)
                    .filter(entity -> entity.getEntityType().getType().equals(entityTypeEnumForFind.getType()))
                    .filter(entity -> capabilityParentDTO.getParents().contains(entity.getEntityId()))
                    .toList();
            log.info("entities: " + entities.stream().map(Entity::getId).collect(Collectors.toList()).toString());
            if (!entities.isEmpty()) {
                Entity entity = entityService.save(Entity.builder()
                        .entityId(entityId)
                        .name(entityName)
                        .link(generateLink(entityTypeEnumForCreate, entityId))
                        .entityType(entityTypeEnumForCreate)
                        .build());
                EntityChange entityChange = entityChangeService.save(
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
                                .entityChange(entityChange)
                                .build())
                        .collect(Collectors.toList());
                notifyService.saveAll(notifies);
            }
        }
    }

    public List<Integer> getAllEntitySubscribeByUserIdAndEntityType(Integer userId, String entityType) {
        User user = userService.findByUserId(userId);
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

    public void deleteSubscribe(Integer entityId, Integer userId, String entityType) {
        User user = userService.findByUserId(userId);
        if (user != null) {
            EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);
            if (entityTypeEnum != null) {
                Entity entity = entityService.findByEntityIdAndEntityType(entityId, entityTypeEnum);
                long countSubscriptions = entity.getSubscribes().stream()
                        .filter(it -> it.getUser().equals(user))
                        .filter(Subscribe::getAutoSubChildren)
                        .count();
                if (EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_CAPABILITY.equals(entityTypeEnum.getType())
                        && countSubscriptions > 0l
                ) {
                    businessCapabilityProcess(entityId, user);
                }
                dropSubscribe(entity, user);
            }
        }
    }

    private void dropSubscribe(Entity entity, User user) {
        if (entity != null) {
            subscribeRepository.deleteByUserAndEntity(user, entity);
            List<EntityChange> entityChanges = entityChangeService.findAllByEntity(entity);
            if (!entityChanges.isEmpty()) {
                notifyService.deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(
                        user.getUserId(),
                        false,
                        false,
                        entityChanges.stream().map(EntityChange::getId).collect(Collectors.toList()));
            }
        }
    }

    private void businessCapabilityProcess(Integer entityId, User user) {
        BusinessCapabilityChildrenDTO businessCapabilityChildrenDTO = capabilityClient.getBusinessCapabilityKidsById(entityId);
        if (businessCapabilityChildrenDTO != null) {
            List<Integer> techCapabilityIds = businessCapabilityChildrenDTO.getTechCapabilities().stream()
                    .map(TechCapabilityShortDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            EntityTypeEnum techCapabilityEntityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
            List<Entity> entities = entityService.findAllByEntityIdInAndEntityType(
                    techCapabilityIds, techCapabilityEntityTypeEnum);


            List<Integer> businessCapabilityIds = businessCapabilityChildrenDTO.getBusinessCapabilities().stream()
                    .map(BusinessCapabilityDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            EntityTypeEnum businessCapabilityEntityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
            entities.addAll(entityService.findAllByEntityIdInAndEntityType(
                    businessCapabilityIds, businessCapabilityEntityTypeEnum));

            entities.forEach(entity -> dropSubscribe(entity, user));
        }
    }

    public void addSubscribe(Integer entityId, Integer userId, String entityType, boolean subChildren) {
        User user = userService.findByUserIdOrCreate(userId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);
        final Entity entity = entityService.getEntityOrCreate(
                generateLink(entityTypeEnum, entityId),
                entityId,
                entityTypeEnum);
        boolean autoSubChildren = entityType.equals("BUSINESS_CAPABILITY") && subChildren;
        findSubscribesOrCreate(entity, user, autoSubChildren);
        if (autoSubChildren) {
            List<Entity> resultEntityList = new ArrayList<>();
            BusinessCapabilityChildrenDTO businessCapabilityChildrenDTO = capabilityClient.getBusinessCapabilityKidsById(entityId);
            List<Integer> techCapabilityIds = businessCapabilityChildrenDTO.getTechCapabilities().stream()
                    .map(TechCapabilityShortDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            techCapabilityIds.forEach(id -> {
                final Entity techEntity = entityService.getEntityOrCreate(
                        generateLink(entityTypeEnumService.getTechCapabilityEntityTypeEnum(), entityId),
                        id,
                        entityTypeEnumService.getTechCapabilityEntityTypeEnum());
                resultEntityList.add(techEntity);
            });
            List<Integer> businessCapabilityIds = businessCapabilityChildrenDTO.getBusinessCapabilities().stream()
                    .map(BusinessCapabilityDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            businessCapabilityIds.forEach(id -> {
                final Entity businessEntity = entityService.getEntityOrCreate(
                        generateLink(entityTypeEnumService.getBusinessCapabilityEntityTypeEnum(), entityId),
                        id,
                        entityTypeEnumService.getBusinessCapabilityEntityTypeEnum());
                resultEntityList.add(businessEntity);
            });

            resultEntityList.forEach(eachEntity -> findSubscribesOrCreate(eachEntity, user, false));

        }
    }

    public void techQueueProcessor(int entityId, String name, String changeType) {
        EntityTypeEnum techEntityTypeEnum = entityTypeEnumService.getTechEntityTypeEnum();
        Entity entity = entityService.findByEntityIdAndEntityType(entityId, techEntityTypeEnum);
        if (entity != null) {
            entity.setName(name);
            List<Subscribe> subscribes = subscribeRepository.findAllByEntity(entity);
            if (!subscribes.isEmpty()) {
                subscribes.forEach(subscribe -> {
                    EntityChange entityChange = EntityChange.builder()
                            .entity(subscribe.getEntity())
                            .dateChange(Timestamp.valueOf(LocalDateTime.now()))
                            .changeType(changeType)
                            .build();
                    entityChange = entityChangeService.save(entityChange);
                    Notify notify = Notify.builder()
                            .user(subscribe.getUser())
                            .webNotify(false)
                            .emailNotify(false)
                            .entityChange(entityChange)
                            .build();
                    notifyService.save(notify);
                });
            }
        }
    }

    private void findSubscribesOrCreate(Entity entity, User user, boolean autoSubChildren) {
        Subscribe subscribe = subscribeRepository.findByUserAndEntity(user, entity);
        if (subscribe == null) {
            subscribeRepository.save(Subscribe.builder()
                    .user(user)
                    .entity(entity)
                    .autoSubChildren(autoSubChildren)
                    .build());
        }
    }

    private String generateLink(EntityTypeEnum entityTypeEnum, Integer entityId) {
        String type = entityTypeEnum.getType().equals(EntityTypeEnum.CapabilitySubscriptionType.TECH_CAPABILITY) ? "TECH" : "BUSINESS";
        return frontendServerUrl + "/fdm?id=" + entityId + "&type=" + type;
    }
}