package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface NotifyRepository extends JpaRepository<Notify, Integer>, JpaSpecificationExecutor<Notify> {
    void deleteAllByUserAndWebNotifyOrEmailNotifyAndEntityChangeIn(User user,
                                                                   Boolean webNotify,
                                                                   Boolean emailNotify,
                                                                   Collection<EntityChange> entityChange
    );

    List<Notify> findByIdInAndUser(List<Integer> ids, User user);
}