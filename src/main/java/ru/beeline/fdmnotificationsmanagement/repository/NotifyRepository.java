package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Integer> {
    void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(User user,
                                                                   Boolean webNotify,
                                                                   Boolean emailNotify,
                                                                   Collection<EntityChange> entityChange
    );

    @Modifying
    @Query("UPDATE Notify n SET n.webNotify = true WHERE n.id IN (SELECT n.id FROM Notify n JOIN User u ON n.user.id = u.id WHERE u.id = :userId AND n.id IN :ids AND n.webNotify = false)")
    void updateWebNotifyByUserIdAndIds(@Param("userId") Integer userId, @Param("ids") List<Integer> ids);
}