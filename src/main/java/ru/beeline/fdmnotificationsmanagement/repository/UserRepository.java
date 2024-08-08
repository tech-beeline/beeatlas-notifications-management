package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.fdmnotificationsmanagement.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Integer userId);
}
