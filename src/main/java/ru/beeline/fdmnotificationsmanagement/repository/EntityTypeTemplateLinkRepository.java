package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeTemplateLink;

import java.util.Optional;

@Repository
public interface EntityTypeTemplateLinkRepository extends JpaRepository<EntityTypeTemplateLink, Integer> {

    Optional<EntityTypeTemplateLink> findByChangeTypeAndEntityType(String changeType, EntityTypeEnum entityType);

}
