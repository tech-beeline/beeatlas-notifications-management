package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChangeSub;

@Repository
public interface EntityChangeSubRepository extends JpaRepository<EntityChangeSub, Long> {
    EntityChangeSub deleteByIdSub(Integer idSub);
}