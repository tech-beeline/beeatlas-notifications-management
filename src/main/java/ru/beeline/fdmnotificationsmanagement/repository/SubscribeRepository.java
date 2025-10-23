package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Entity;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAllByEntity(Entity entity);

    Subscribe findByUserAndEntity(User user, Entity entity);

    List<Subscribe> findAllByUser(User user);

    List<Subscribe> findByUserAndEntityIn(User user, List<Entity> EntityList);

    List<Subscribe> findByAutoSubChildrenTrue();

    @Modifying
    @Query(value = "DELETE FROM notification.subscribe " +
            "WHERE notification.subscribe.user_id = :userId " +
            "AND notification.subscribe.entity_id IN (:entityIds)", nativeQuery = true)
    void deleteAllByUserIdAndEntityIdIn(@Param("userId") Integer userId, @Param("entityIds") List<Integer> entityIds);

    @Query("SELECT s.entity.entityId FROM Subscribe s WHERE s.user = :user AND s.entity.entityType = :entityType")
    List<Integer> findAllEntityIdsByUserAndEntityEntityType(@Param("user") User user, @Param("entityType") EntityTypeEnum entityType);
}