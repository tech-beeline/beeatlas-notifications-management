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

    @Value("${integration.frontend-server-url}")
    private String frontendServerUrl;

    @Autowired
    private EntityTypeEnumService entityTypeEnumService;

    @Autowired
    private EntityChangeRepository entityChangeRepository;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private CapabilityIntegrationService capabilityIntegrationService;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private UserRepository userRepository;

    public void updateSubscribeBusinessCapability(Integer entityId) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, null);
    }

    public void updateSubscribeTechCapability(Integer entityId, String entityName) {
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getTechCapabilityEntityTypeEnum();
        updateSubscribe(entityId, entityTypeEnum, entityName);
    }

    public void createSubscribeTechCapability(Integer entityId, String entityName) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getTechCapabilityParents(entityId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        if (capabilityParentDTO != null)
            createSubscribe(entityId, capabilityParentDTO, entityTypeEnum, entityName);
    }

    public void createSubscribeBusinessCapability(Integer entityId) {
        CapabilityParentDTO capabilityParentDTO = capabilityIntegrationService.getBusinessCapabilityParents(entityId);
        EntityTypeEnum entityTypeEnum = entityTypeEnumService.getBusinessCapabilityEntityTypeEnum();
        createSubscribe(entityId, capabilityParentDTO, entityTypeEnum, null);
    }

    private void updateSubscribe(Integer entityId, EntityTypeEnum entityTypeEnum, String entityName) {
        Entity entity = entityRepository.findByEntityIdAndEntityType(entityId, entityTypeEnum);
        if (entity != null) {
            if (entityName != null
                    && EntityTypeEnum.CapabilitySubscriptionType.TECH.equals(entityTypeEnum.getType())
                    && !entityName.equals(entity.getName())) {
                entity.setName(entityName);
                entityRepository.save(entity);
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
        if (!subscribes.isEmpty()) {
            List<Entity> entities = subscribes.stream()
                    .map(Subscribe::getEntity)
                    .filter(entity -> entity.getEntityType().equals(entityTypeEnum))
                    .filter(entity -> capabilityParentDTO.getParents().contains(entityId.longValue()))
                    .toList();
            if (!entities.isEmpty()) {
                Entity entity = entityRepository.save(Entity.builder()
                        .entityId(entityId)
                        .name(entityName)
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