package ru.beeline.fdmnotificationsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.fdmnotificationsmanagement.domain.Subscribe;

import java.util.List;

@Repository
public interface SubscribeRuleRepository extends JpaRepository<Subscribe, Long> {
    @Query(value = "SELECT COUNT(*) FROM notification.subscribe_rule " +
            "JOIN notification.entity_auto_subscribe ON notification.subscribe_rule.auto_sub_id = notification.entity_auto_subscribe.id " +
            "JOIN notification.\"user\" ON notification.\"user\".id = notification.entity_auto_subscribe.user_id " +
            "JOIN notification.entity_type_enum ON notification.entity_type_enum.id = notification.entity_auto_subscribe.entity_type_id " +
            "WHERE notification.subscribe_rule.parameter_name = :parameterName " +
            "AND notification.subscribe_rule.parameter_value = :parameterValue " +
            "AND notification.\"user\".user_id = :userId " +
            "AND notification.entity_type_enum.type = :entityTypeName ", nativeQuery = true)
    Long countByParameterNameAndParameterValueAndUserIdAndEntityTypeName(@Param("parameterName") String parameterName,
                                                                         @Param("parameterValue") String parameterValue,
                                                                         @Param("userId") Integer userId,
                                                                         @Param("entityTypeName") String entityTypeName);

//    @Query(value = "SELECT * FROM notification.subscribe_rule " +
//            "JOIN notification.entity_auto_subscribe ON notification.subscribe_rule.auto_sub_id = notification.entity_auto_subscribe.id " +
//            "JOIN notification.entity_type_enum ON notification.entity_type_enum.id = notification.entity_auto_subscribe.entity_type_id " +
//            "WHERE notification.subscribe_rule.parameter_name = :parameterName " +
//            "AND notification.subscribe_rule.parameter_value = :parameterValue " +
//            "AND notification.entity_type_enum.type = :entityTypeName ", nativeQuery = true)
//    List<Subscribe> getByParameterNameAndParameterValueAndEntityTypeName(@Param("parameterName") String parameterName,
//                                                                         @Param("parameterValue") String parameterValue,
//                                                                         @Param("entityTypeName") String entityTypeName);
//
//    Subscribe findByParameterNameAndParameterValueAndAutoSubId(String parameterName,
//                                                               String parameterValue,
//                                                               Integer autoSubId);
}