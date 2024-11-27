package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Integer>, JpaSpecificationExecutor<Notify> {
    @Modifying
    @Query(value = "DELETE FROM notification.notify " +
            "WHERE notification.notify.user_id = :userId " +
            "AND (notification.notify.web_notify = :webNotify OR notification.notify.email_notify = :emailNotify) " +
            "AND notification.notify.change_id IN (SELECT id FROM notification.entity_change WHERE entity_id IN :entityIds);", nativeQuery = true)
    void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(@Param("userId") Integer userId,
                                                                   @Param("webNotify") Boolean webNotify,
                                                                   @Param("emailNotify") Boolean emailNotify,
                                                                   @Param("entityIds") Collection<Integer> entityIds);

    List<Notify> findByIdInAndUser(List<Integer> ids, User user);
}