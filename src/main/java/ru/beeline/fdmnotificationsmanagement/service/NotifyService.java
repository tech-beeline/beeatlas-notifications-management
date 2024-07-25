package ru.beeline.fdmnotificationsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.User;
import ru.beeline.fdmnotificationsmanagement.domain.specification.NotifySpecifications;
import ru.beeline.fdmnotificationsmanagement.dto.UnreadNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.exception.EntityNotFoundException;
import ru.beeline.fdmnotificationsmanagement.repository.NotifyRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    public void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(User user,
                                                                          Boolean webNotify,
                                                                          Boolean emailNotify,
                                                                          Collection<EntityChange> entityChanges) {
        notifyRepository.deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(
                user,
                webNotify,
                emailNotify,
                entityChanges);
    }

    public List<UnreadNotifyDTO> getNotify(Integer userId,
                                           Timestamp afterDate,
                                           Timestamp beforeDate,
                                           String type,
                                           Boolean wasNotify,
                                           Integer page) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        final Specification<Notify> specification = getNotifySpecification(afterDate, beforeDate, type, wasNotify, user);

        PageRequest pageRequest = PageRequest.of(page != null ? page : 0, 20);
        Page<Notify> notifyPage = notifyRepository.findAll(specification, pageRequest);

        return notifyPage.getContent().stream()
                .map(this::mapUnreadNotifyDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public void patchNotify(Integer userId, List<Integer> notifyIds) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        notifyRepository.updateWebNotifyByUserIdAndIds(userId, notifyIds);
    }

}