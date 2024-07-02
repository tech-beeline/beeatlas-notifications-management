package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAllByEntity(Entity entity);

    List<Subscribe> findAllByUser(User user);

    Subscribe findByUserAndEntity(User user, Entity entity);

    List<Subscribe> findByAutoSubChildrenTrue();

    void deleteByUserAndEntity(User user, Entity entity);
}