package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
//    @Query(value = "SELECT COUNT(*) FROM notification.subscribe_rule " +
//            "JOIN notification.entity ON notification.subscribe_rule.auto_sub_id = notification.entity.id " +
//            "JOIN notification.\"user\" ON notification.\"user\".id = notification.entity.user_id " +
//            "JOIN notification.entity_type_enum ON notification.entity_type_enum.id = notification.entity.entity_type_id " +
//            "WHERE notification.subscribe_rule.parameter_name = :parameterName " +
//            "AND notification.subscribe_rule.parameter_value = :parameterValue " +
//            "AND notification.\"user\".user_id = :userId " +
//            "AND notification.entity_type_enum.type = :entityTypeName ", nativeQuery = true)
//    Long countByParameterNameAndParameterValueAndUserIdAndEntityTypeName(@Param("parameterName") String parameterName,
//                                                                         @Param("parameterValue") String parameterValue,
//                                                                         @Param("userId") Integer userId,
//                                                                         @Param("entityTypeName") String entityTypeName);

    @Query(value = "SELECT notification.subscribe.entity_id FROM notification.subscribe " +
            "JOIN notification.entity ON notification.subscribe.entity_id = notification.entity.id " +
            "JOIN notification.entity_type_enum ON notification.entity_type_enum.id = notification.entity.entity_type_id " +
            "WHERE notification.subscribe.user_id = :userId " +
            "AND notification.entity_type_enum.type = :entityTypeName ", nativeQuery = true)
    List<Integer> getByUserIdAndEntityTypeName(@Param("userId") Integer userId,
                                                 @Param("entityTypeName") String entityTypeName);

}