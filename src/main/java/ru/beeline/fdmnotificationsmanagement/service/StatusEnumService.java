package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.StatusEnum;
import ru.beeline.fdmnotificationsmanagement.repository.StatusEnumRepository;

import java.util.Objects;

@Service
public class StatusEnumService {
    private static StatusEnum waitNotifyStatusEnum = null;

    @Autowired
    private StatusEnumRepository statusEnumRepository;


    public StatusEnum getWaitNotifyStatusEnum() {
        if (Objects.isNull(waitNotifyStatusEnum)) {
            waitNotifyStatusEnum = statusEnumRepository.findByStatus("WAIT_NOTIFY");
        }
        return waitNotifyStatusEnum;
    }
}