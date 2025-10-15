package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.repository.EntityRepository;

import java.util.List;

@Service
public class EntityService {

    @Autowired
    private EntityRepository entityRepository;


    public Entity save(Entity entity) {
        return entityRepository.save(entity);
    }


    public Entity findByEntityId(Integer entityId) {
        return entityRepository.findByEntityId(entityId);
    }

    public Entity findByEntityIdAndEntityType(Integer entityId, EntityTypeEnum entityType) {
        return entityRepository.findByEntityIdAndEntityType(entityId, entityType);
    }

    public List<Entity> findAllByEntityIdInAndEntityType(List<Integer> entityIds, EntityTypeEnum entityType) {
        return entityRepository.findAllByEntityIdInAndEntityType(entityIds, entityType);
    }

    public Entity getEntityOrCreate(String link, Integer id, EntityTypeEnum entityTypeEnum, String name) {
        Entity techEntity = findByEntityIdAndEntityType(
                id, entityTypeEnum);
        if (techEntity == null) {
            techEntity = save(Entity.builder()
                    .entityId(id)
                    .link(link)
                    .name(name)
                    .entityType(entityTypeEnum)
                    .build());
        }else {
            if (!techEntity.getName().equals(name)) {
                techEntity.setName(name);
                save(techEntity);
            }
        }
        return techEntity;
    }

}