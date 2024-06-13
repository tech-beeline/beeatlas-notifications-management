package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.EntityChange;
import ru.beeline.fdmnotificationsmanagement.domain.Notify;
import ru.beeline.fdmnotificationsmanagement.domain.User;

import java.util.Collection;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Integer> {
    void deleteAllByUserAndEntityChangeInAndWebNotifyOrEmailNotify(User user,
                                                                   Collection<EntityChange> entityChange,
                                                                   Boolean webNotify,
                                                                   Boolean emailNotify);
}