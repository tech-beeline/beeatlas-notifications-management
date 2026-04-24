/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.dto.auth.EmailResponseDTO;
import ru.beeline.fdmnotificationsmanagement.dto.auth.UserProfileShortDTO;
import ru.beeline.fdmnotificationsmanagement.client.AuthClient;
import ru.beeline.fdmnotificationsmanagement.domain.*;
import ru.beeline.fdmnotificationsmanagement.domain.specification.BusinessNotifySpecifications;
import ru.beeline.fdmnotificationsmanagement.domain.specification.NotifySpecifications;
import ru.beeline.fdmnotificationsmanagement.dto.BusinessNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.dto.ChangeTypeIdDTO;
import ru.beeline.fdmnotificationsmanagement.dto.EntityTypeIdDTO;
import ru.beeline.fdmnotificationsmanagement.dto.UnreadNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.exception.BadRequestException;
import ru.beeline.fdmnotificationsmanagement.exception.EntityNotFoundException;
import ru.beeline.fdmnotificationsmanagement.exception.ForbiddenException;
import ru.beeline.fdmnotificationsmanagement.repository.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NotifyService {

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private BusinessEventEnumRepository businessEventEnumRepository;

    @Autowired
    private BusinessNotifyRepository businessNotifyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChangeTypeEnumRepository changeTypeEnumRepository;

    @Autowired
    private EntityTypeEnumRepository entityTypeEnumRepository;

    @Autowired
    private EntityTypeTemplateLinkRepository entityTypeTemplateLinkRepository;


    public List<Notify> saveAll(List<Notify> notifies) {
        return notifyRepository.saveAll(notifies);
    }

    public Notify save(Notify notify) {
        return notifyRepository.save(notify);
    }

    public void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(Integer user,
                                                                          Boolean webNotify,
                                                                          Boolean emailNotify,
                                                                          Collection<Integer> entityIds) {
        notifyRepository.deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(user,
                webNotify,
                emailNotify,
                entityIds);
    }

    public Page<UnreadNotifyDTO> getNotify(Integer userId, Timestamp afterDate, Timestamp beforeDate,
                                           String type, Boolean wasNotify, Integer page) {
        if (type != null) {
            try {
                EntityTypeEnum.CapabilitySubscriptionType.valueOf(type);
            } catch (Exception e) {
                throw new BadRequestException("400 Неверно указан тип сущности");
            }
        }
        User user = userService.findByUserId(userId);
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0,
                20,
                Sort.by("entityChange.dateChange").descending());
        if (user == null) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
        final Specification<Notify> specification = getNotifySpecification(afterDate,
                beforeDate,
                type,
                wasNotify,
                user);
        Page<Notify> notifyPage = notifyRepository.findAll(specification, pageRequest);
        if (!notifyPage.isEmpty()) {

            List<UnreadNotifyDTO> result = notifyPage.stream()
                    .map(this::mapUnreadNotifyDTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(result, pageRequest, notifyPage.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
    }

    private UnreadNotifyDTO mapUnreadNotifyDTO(Notify notify) {
        UnreadNotifyDTO notificationDto = new UnreadNotifyDTO();
        notificationDto.setId(notify.getId());
        notificationDto.setWebNotify(notify.getWebNotify());
        notificationDto.setChangeDescription(
                changeTypeEnumRepository.findChangeTypeEnumByName(notify.getEntityChange().getChangeType()).getDescription());
        EntityChange entityChange = notify.getEntityChange();
        if (entityChange != null) {
            notificationDto.setChangeDate(entityChange.getDateChange());
            notificationDto.setChangeType(entityChange.getChangeType());
            notificationDto.setChildrenEntityId(entityChange.getChildrenEntityId());
            Entity entity = entityChange.getEntity();
            if (entity != null) {
                Optional<EntityTypeTemplateLink> entityTypeTemplateLink = entityTypeTemplateLinkRepository.findByChangeTypeAndEntityType(
                        entityChange.getChangeType(),
                        entity.getEntityType());
                entityTypeTemplateLink.ifPresent(typeTemplateLink -> notificationDto.setLinkTemplate(typeTemplateLink.getLinkTemplate()));
                if (entityTypeTemplateLink.isPresent()) {
                    notificationDto.setLinkTemplate(entityTypeTemplateLink.get().getLinkTemplate());
                } else {
                    notificationDto.setLinkTemplate(entity.getEntityType().getBaseLinkTemplate());
                }
                notificationDto.setAlias(entity.getEntityType().getAlias());
                notificationDto.setEntityId(entity.getEntityId());
                notificationDto.setEntityName(entity.getName());
                notificationDto.setEntityLink(entity.getLink());
                notificationDto.setEntityType(entity.getEntityType().getType());
            }
        }
        return notificationDto;
    }

    private static Specification<Notify> getNotifySpecification(Timestamp afterDate,
                                                                Timestamp beforeDate,
                                                                String type,
                                                                Boolean wasNotify,
                                                                User user) {
        Specification<Notify> specification = Specification.where(NotifySpecifications.hasUserId(user.getId()));
        if (wasNotify != null) {
            specification = specification.and(NotifySpecifications.hasWebNotify(wasNotify));
        }
        if (afterDate != null) {
            specification = specification.and(NotifySpecifications.hasChangeDateAfter(afterDate));
        }
        if (beforeDate != null) {
            specification = specification.and(NotifySpecifications.hasChangeDateBefore(beforeDate));
        }
        if (type != null) {
            specification = specification.and(NotifySpecifications.hasEntityType(type));
        }
        return specification;
    }

    public void postGroupNotify(String entityType, Integer entityId, String role, String name) {

        List<UserProfileShortDTO> profiles = authClient.getUserProfilesByRole(role);

        if (profiles == null || profiles.isEmpty()) {
            throw new BadRequestException("Нет получателей для нотификаций");
        }

        profiles.forEach(profile -> postNotify(profile.getId(), entityType, entityId, name));
    }

    public void postNotify(Integer userId, String entityType, Integer entityId, String name) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            user = new User();
            try {
                EmailResponseDTO authResponse = authClient.getEmailByUserID(userId);
                user.setUserId(userId);
                user.setEmail(authResponse.getEmail());
                user = userRepository.save(user);
            } catch (Exception e) {
                log.error("Ошибка при получении email из AuthClient: {}", e.getMessage());
                throw new ForbiddenException("Не удалось получить email пользователя");
            }
        }
        BusinessEventEnum businessEventEnum = businessEventEnumRepository.findByName(entityType);
        if (businessEventEnum == null) {
            log.error("Тип события {} не найден", entityType);
            throw new BadRequestException("Неверный тип события");
        }
        BusinessNotify businessNotify = new BusinessNotify();
        businessNotify.setUser(user);
        businessNotify.setEntityId(entityId);
        businessNotify.setEntityType(businessEventEnum);
        businessNotify.setWebNotify(false);
        businessNotify.setCreatedDate(LocalDateTime.now());
        businessNotify.setName(name);
        businessNotifyRepository.save(businessNotify);
        log.info(businessNotify.toString() + "saved");
        log.info("method postNotify completed ");
    }

    public void patchNotify(Integer userId, String notifyType, List<Integer> notifyIds) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (notifyIds == null) {
            throw new BadRequestException("Параметр notifyIds не найден");
        }
        validateNotifyType(notifyType);
        List<Notify> notifies = notifyRepository.findByIdInAndUser(notifyIds, user);
        notifies.forEach(notify -> {
            switch (notifyType) {
                case "web":
                    notify.setWebNotify(true);
                    break;
                case "email":
                    notify.setEmailNotify(true);
                    break;
                case "all":
                    notify.setWebNotify(true);
                    notify.setEmailNotify(true);
                    break;
            }
        });
        notifyRepository.saveAll(notifies);
    }

    private void validateNotifyType(String notifyType) {
        if (!List.of("web", "email", "all").contains(notifyType)) {
            throw new BadRequestException("Передан неверный формат нотификаций");
        }
    }

    public Page<BusinessNotifyDTO> getBusinessNotify(Integer userId,
                                                     Timestamp afterDate,
                                                     Timestamp beforeDate,
                                                     String type,
                                                     Boolean wasNotify,
                                                     Integer page) {
        if (type != null) {
            BusinessEventEnum businessEventEnum = businessEventEnumRepository.findByName(type);
            if (businessEventEnum == null) {
                throw new BadRequestException("400 Неверно указан тип сущности");
            }
        }
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0,
                20,
                Sort.by(Sort.Order.desc("createdDate"), Sort.Order.asc("id")));
        User user = userService.findByUserId(userId);
        if (user == null) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
        final Specification<BusinessNotify> specification = getBusinessNotifySpecification(afterDate == null ? null : afterDate.toLocalDateTime(),
                beforeDate == null ? null : beforeDate.toLocalDateTime(),
                type,
                wasNotify,
                user);
        Page<BusinessNotify> notifyPage = businessNotifyRepository.findAll(specification, pageRequest);
        if (!notifyPage.isEmpty()) {
            List<BusinessNotifyDTO> result = notifyPage.stream()
                    .map(this::mapBusinessNotifyDTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(result, pageRequest, notifyPage.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
    }

    private BusinessNotifyDTO mapBusinessNotifyDTO(BusinessNotify businessNotify) {
        BusinessNotifyDTO businessNotifyDTO = new BusinessNotifyDTO();
        businessNotifyDTO.setId(businessNotify.getId());
        businessNotifyDTO.setWebNotify(businessNotify.getWebNotify());
        businessNotifyDTO.setCreatedDate(businessNotify.getCreatedDate());
        businessNotifyDTO.setEntityId(businessNotify.getEntityId());
        businessNotifyDTO.setEntityTypeId(businessNotify.getEntityType());
        businessNotifyDTO.setName(businessNotify.getName());
        return businessNotifyDTO;
    }

    private static Specification<BusinessNotify> getBusinessNotifySpecification(LocalDateTime afterDate,
                                                                                LocalDateTime beforeDate,
                                                                                String type,
                                                                                Boolean wasNotify,
                                                                                User user) {
        Specification<BusinessNotify> specification = Specification.where(BusinessNotifySpecifications.hasUserId(user.getId()));
        if (wasNotify != null) {
            specification = specification.and(BusinessNotifySpecifications.hasWebNotify(wasNotify));
        }
        if (afterDate != null) {
            specification = specification.and(BusinessNotifySpecifications.hasChangeDateAfter(afterDate));
        }
        if (beforeDate != null) {
            specification = specification.and(BusinessNotifySpecifications.hasChangeDateBefore(beforeDate));
        }
        if (type != null) {
            specification = specification.and(BusinessNotifySpecifications.hasEntityType(type));
        }
        return specification;
    }

    public void pathBusinessNotify(Integer userId, List<Integer> ids) {
        User users = userRepository.findByUserId(userId);
        if (users == null) {
            throw new EntityNotFoundException("Запись с данным User Id не найдена");
        }
        for (Integer id : ids) {
            Optional<BusinessNotify> optionalBusinessNotify = businessNotifyRepository.findById(id);
            if (optionalBusinessNotify.isPresent()) {
                BusinessNotify businessNotify = optionalBusinessNotify.get();
                if (Objects.equals(businessNotify.getUser().getId(), users.getId())) {
                    businessNotify.setWebNotify(true);
                    businessNotifyRepository.save(businessNotify);
                }
            }
        }
    }

    public List<ChangeTypeIdDTO> getChangeTypes() {
        return changeTypeEnumRepository.findAll()
                .stream()
                .map(element -> ChangeTypeIdDTO.builder()
                        .id(element.getId())
                        .name(element.getName())
                        .description(element.getDescription())
                        .build())
                .toList();
    }

    public List<EntityTypeIdDTO> getEntityTypes() {
        return entityTypeEnumRepository.findAll()
                .stream()
                .map(element -> EntityTypeIdDTO.builder()
                        .id(element.getId())
                        .type(element.getType().toString())
                        .alias(element.getAlias())
                        .baseLinkTemplate(element.getBaseLinkTemplate())
                        .build())
                .toList();
    }
}