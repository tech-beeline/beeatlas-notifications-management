package ru.beeline.fdmnotificationsmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.repository.EntityChangeRepository;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class EntityChangeService {

    @Autowired
    private EntityChangeRepository entityChangeRepository;

    public EntityChange save(EntityChange entityChanges) {
        return entityChangeRepository.save(entityChanges);
    }
}