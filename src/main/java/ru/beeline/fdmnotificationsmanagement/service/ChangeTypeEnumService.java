package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.domain.ChangeTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.repository.ChangeTypeEnumRepository;
import ru.beeline.fdmnotificationsmanagement.repository.EntityTypeEnumRepository;

import java.util.Objects;

@Service
public class ChangeTypeEnumService {
    private static ChangeTypeEnum updateChangeTypeEnum = null;
    private static ChangeTypeEnum createChangeTypeEnum = null;

    @Autowired
    private ChangeTypeEnumRepository changeTypeEnumRepository;


    public ChangeTypeEnum getUpdateChangeTypeEnum() {
        if (Objects.isNull(updateChangeTypeEnum)) {
            updateChangeTypeEnum = changeTypeEnumRepository.findByChangeType("UPDATE");
        }
        return updateChangeTypeEnum;
    }

    public ChangeTypeEnum getCreateChangeTypeEnum() {
        if (Objects.isNull(createChangeTypeEnum)) {
            createChangeTypeEnum = changeTypeEnumRepository.findByChangeType("CREATE");
        }
        return createChangeTypeEnum;
    }

}