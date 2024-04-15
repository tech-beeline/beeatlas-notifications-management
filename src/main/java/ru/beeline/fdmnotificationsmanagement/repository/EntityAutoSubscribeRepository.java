package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityAutoSubscribe;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChangeSub;

import java.util.List;

@Repository
public interface EntityAutoSubscribeRepository extends JpaRepository<EntityAutoSubscribe, Integer> {
    List<EntityAutoSubscribe> findAllByIdIn(List<Integer> ids);
}