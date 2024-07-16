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
import ru.beeline.fdmnotificationsmanagement.exception.BadRequestException;
import ru.beeline.fdmnotificationsmanagement.exception.EntityNotFoundException;
import ru.beeline.fdmnotificationsmanagement.repository.EntityChangeRepository;
import ru.beeline.fdmnotificationsmanagement.repository.NotifyRepository;
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
    private EntityChangeRepository entityChangeRepository;

    @Autowired
    private NotifyRepository notifyRepository;

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
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        if (capabilityParentDTO != null)
            createSubscribe(entityId, capabilityParentDTO, entityTypeEnum, entityName);
    }

    public void createSubscribeBusinessCapability(Integer entityId, String name) {
        CapabilityParentDTO capabilityParentDTO = capabilityClient.getBusinessCapabilityParents(entityId);
        log.info("capabilityParentIDs: " + capabilityParentDTO.getParents().toString());
        if(!capabilityParentDTO.getParents().isEmpty()) {
            EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
            createSubscribe(entityId, capabilityParentDTO, entityTypeEnum, name);
        }
    }

    private void updateSubscribe(Integer entityId, EntityTypeEnum entityTypeEnum, String entityName) {
        Entity entity = entityService.findByEntityIdAndEntityType(entityId, entityTypeEnum);
        if (entity != null) {
            if (EntityTypeEnum.CapabilitySubscriptionType.TECH.equals(entityTypeEnum.getType())
                    && !entityName.equals(entity.getName())) {
                entity.setName(entityName);
                entityService.save(entity);
            }
            List<Subscribe> subscribes = subscribeRepository.findAllByEntity(entity);
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
                                 EntityTypeEnum entityTypeEnum, String entityName) {
        List<Subscribe> subscribes = subscribeRepository.findByAutoSubChildrenTrue();
        log.info("subscribes: " + subscribes.stream().map(Subscribe::getId).collect(Collectors.toList()).toString());
        if (!subscribes.isEmpty()) {
            List<Entity> entities = subscribes.stream()
                    .map(Subscribe::getEntity)
                    .filter(entity -> entity.getEntityType().getType().equals(entityTypeEnum.getType()))
                    .filter(entity -> capabilityParentDTO.getParents().contains(entity.getEntityId()))
                    .toList();
            log.info("entities: " + entities.stream().map(Entity::getId).collect(Collectors.toList()).toString());
            if (!entities.isEmpty()) {
                Entity entity = entityService.save(Entity.builder()
                        .entityId(entityId)
                        .name(entityName)
                        .link(generateLink(entityTypeEnum, entityId))
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
                businessCapabilityProcess(entityId, user);
                Entity entity = entityService.findByIdAndEntityType(entityId, entityTypeEnum);
                dropSubscribe(entityId, entity, user);
            }
        }
    }

    private void dropSubscribe(Integer entityId, Entity entity, User user) {
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

    private void businessCapabilityProcess(Integer entityId, User user) {
        BusinessCapabilityChildrenDTO businessCapabilityChildrenDTO = capabilityClient.getBusinessCapabilityKidsById(entityId);

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

        entities.forEach(entity -> dropSubscribe(entity.getEntityId(), entity, user));
    }

    public void addSubscribe(Integer entityId, Integer userId, String entityType, boolean subChildren) {
        User user = userService.findByUserIdOrCreate(userId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getEntityTypeEnumByTypeName(entityType);
        if (entityTypeEnum == null) {
            throw new BadRequestException("Неверно указан тип сущности");
        }
        final Entity entity = entityService.getEntityOrCreate(
                generateLink(entityTypeEnum, entityId),
                entityId,
                entityTypeEnum);
        findSubscribesOrCreate(entity, user, entityTypeEnum.getType().name().equals("BUSINESS_CAPABILITY") && subChildren);
        if (EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_CAPABILITY.equals(entityTypeEnum.getType()) && subChildren) {
            List<Entity> resultEntityList = new ArrayList<>();
            BusinessCapabilityChildrenDTO businessCapabilityChildrenDTO = capabilityClient.getBusinessCapabilityKidsById(entityId);
            EntityTypeEnum techCapabilityEntityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
            List<Integer> techCapabilityIds = businessCapabilityChildrenDTO.getTechCapabilities().stream()
                    .map(TechCapabilityShortDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            techCapabilityIds.forEach(id -> {
                final Entity techEntity = entityService.getEntityOrCreate(
                        generateLink(entityTypeEnum, entityId),
                        id,
                        techCapabilityEntityTypeEnum);
                resultEntityList.add(techEntity);
            });


            EntityTypeEnum businessCapabilityEntityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
            List<Integer> businessCapabilityIds = businessCapabilityChildrenDTO.getBusinessCapabilities().stream()
                    .map(BusinessCapabilityDTO::getId)
                    .map(Math::toIntExact)
                    .collect(Collectors.toList());
            businessCapabilityIds.forEach(id -> {
                final Entity businessEntity = entityService.getEntityOrCreate(
                        generateLink(entityTypeEnum, entityId),
                        id,
                        businessCapabilityEntityTypeEnum);
                resultEntityList.add(businessEntity);
            });

            resultEntityList.forEach(eachEntity -> findSubscribesOrCreate(eachEntity, user, false));

        }
    }

    public void patchNotify(Integer userId, List<Integer> notifyIds) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        notifyRepository.updateWebNotifyByUserIdAndIds(userId, notifyIds);
    }

    public void techQueueProcessor(int entityId, String name, String changeType) {
        EntityTypeEnum techEntityTypeEnum = entityTypeEnumService.getTechEntityTypeEnum();
        Entity entity = entityService.findByEntityIdAndEntityType(entityId, techEntityTypeEnum);
        if (entity != null) {
            entity.setName(name);
            List<Subscribe> subscribes = subscribeRepository.findAllByEntity(entity);
            if (!subscribes.isEmpty()) {
                List<EntityChange> entityChanges = subscribes.stream()
                        .map(subscribe -> EntityChange.builder()
                                .entity(subscribe.getEntity())
                                .dateChange(Timestamp.valueOf(LocalDateTime.now()))
                                .changeType(changeType)
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
        String type = entityTypeEnum.getType().name().equals("TECH_CAPABILITY") ? "TECH" : "BUSINESS";
        return frontendServerUrl + "//fdm?id=" + entityId + "&type=" + type;
    }
}