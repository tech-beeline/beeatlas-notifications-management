package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAllByEntity(Entity entity);

    List<Subscribe> findAllByUser(User user);

    Subscribe findByUserAndEntity(User user, Entity entity);

    List<Subscribe> findByUserAndEntityIn(User user,List<Entity> EntityList);

    List<Subscribe> findByAutoSubChildrenTrue();

    @Modifying
    @Query(value = "DELETE FROM notification.subscribe " +
            "WHERE notification.subscribe.user_id = :userId " +
            "AND notification.subscribe.entity_id = :entityId", nativeQuery = true)
    void deleteByUserAndEntity(@Param("userId") Integer userId,@Param("entityId") Integer entityId);
}