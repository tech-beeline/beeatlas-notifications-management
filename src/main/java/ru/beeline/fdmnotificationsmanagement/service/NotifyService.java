package ru.beeline.fdmnotificationsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.User;
import ru.beeline.fdmnotificationsmanagement.domain.specification.NotifySpecifications;
import ru.beeline.fdmnotificationsmanagement.dto.UnreadNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.exception.BadRequestException;
import ru.beeline.fdmnotificationsmanagement.exception.EntityNotFoundException;
import ru.beeline.fdmnotificationsmanagement.repository.NotifyRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NotifyService {

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private UserService userService;


    public List<Notify> saveAll(List<Notify> notifies) {
        return notifyRepository.saveAll(notifies);
    }

    public Notify save(Notify notify) {
        return notifyRepository.save(notify);
    }

    public void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(Integer user,
                                                                          Boolean webNotify,
                                                                          Boolean emailNotify,
                                                                          Collection<Integer> entityChangesIds) {
        notifyRepository.deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(
                user,
                webNotify,
                emailNotify,
                entityChangesIds);
    }

    public Page<UnreadNotifyDTO> getNotify(Integer userId,
                                           Timestamp afterDate,
                                           Timestamp beforeDate,
                                           String type,
                                           Boolean wasNotify,
                                           Integer page) {
        if(type!=null) {
            try {
                EntityTypeEnum.CapabilitySubscriptionType.valueOf(type);
            } catch (Exception e) {
                throw new BadRequestException("400 Неверно указан тип сущности");
            }
        }
        User user = userService.findByUserId(userId);
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0, 20, Sort.by("entityChange.dateChange").descending());

        if (user == null) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        final Specification<Notify> specification = getNotifySpecification(afterDate, beforeDate, type, wasNotify, user);

        Page<Notify> notifyPage = notifyRepository.findAll(specification, pageRequest);

        if (!notifyPage.isEmpty()) {
            List<UnreadNotifyDTO> result = notifyPage.stream().map(this::mapUnreadNotifyDTO).collect(Collectors.toList());
            return new PageImpl<>(result, pageRequest, notifyPage.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
    }

    private UnreadNotifyDTO mapUnreadNotifyDTO(Notify notify) {
        UnreadNotifyDTO notificationDto = new UnreadNotifyDTO();
        notificationDto.setId(notify.getId());
        notificationDto.setWebNotify(notify.getWebNotify());
        EntityChange entityChange = notify.getEntityChange();
        if (entityChange != null) {
            notificationDto.setChangeDate(entityChange.getDateChange());
            notificationDto.setChangeType(entityChange.getChangeType());
            Entity entity = entityChange.getEntity();
            if (entity != null) {
                notificationDto.setEntityId(entity.getEntityId());
                notificationDto.setEntityName(entity.getName());
                notificationDto.setEntityLink(entity.getLink());
                notificationDto.setEntityType(entity.getEntityType().getType().name());
            }
        }
        return notificationDto;
    }

    private static Specification<Notify> getNotifySpecification(Timestamp afterDate, Timestamp beforeDate, String type, Boolean wasNotify, User user) {
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
}