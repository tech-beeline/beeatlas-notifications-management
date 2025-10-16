package ru.beeline.fdmnotificationsmanagement.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.beeline.fdmnotificationsmanagement.repository.ChangeTypeEnumRepository;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;


@Slf4j
@Component
@EnableRabbit
public class ChangeTechCapabilityConsumer {

    @Autowired
    CapabilitySubscribeService capabilitySubscribeService;
    @Autowired
    ChangeTypeEnumRepository changeTypeEnumRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${queue.tech-queue.name}")
    public void techQueue(String message) {
        log.info("Received from tech_queue: " + message, new String(message.getBytes()));
        try {
            JsonNode jsonArray = objectMapper.readTree(message);
            if (jsonArray.isArray()) {
                for (JsonNode jsonNode : jsonArray) {
                    if (jsonNode.has("entity_id") && jsonNode.has("change_type") && jsonNode.has("name")) {
                        capabilitySubscribeService.techQueueProcessor(jsonNode.get("entity_id").asInt(),
                                                                      jsonNode.get("name").asText(),
                                                                      jsonNode.get("change_type").asText(),
                                                                      null);
                    } else {
                        log.error("Message does not match the required format");
                    }
                }
            } else {
                log.error("Message is not an array");
            }
        } catch (Exception e) {
            log.error("Internal server Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "${queue.change-tech-capability.name}")
    public void changeTechCapabilityQueue(String message) {
        log.info("Received from change-tech-capability: " + message, new String(message.getBytes()));
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.has("entity_id") && jsonNode.has("change_type") && jsonNode.has("name")) {
                String changeType = jsonNode.get("change_type").asText();
                String name = jsonNode.get("name").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType, null);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribeTechCapability(entityId, name, null);
                        break;
                }
            } else {
                log.error("Message does not match the required format");
            }
        } catch (Exception e) {
            log.error("Internal server Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "${queue.change-business-capability.name}")
    public void changeBusinessCapabilityQueue(String message) {
        log.info("Received from change-business-capability: " + message, new String(message.getBytes()));
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.has("entity_id") && jsonNode.has("change_type") && jsonNode.has("name")) {
                String changeType = jsonNode.get("change_type").asText();
                String name = jsonNode.get("name").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name, changeType, null);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribeBusinessCapability(entityId, name, null);
                        break;
                }
            } else {
                log.error("Message does not match the required format");
            }
        } catch (Exception e) {
            log.error("Internal server Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "${queue.notification.name}")
    public void notificationQueue(String message) {
        log.info("Received message: " + message);

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(message);
            if (!jsonNode.has("entityId") || !jsonNode.has("changeType") || !jsonNode.has("entityType")) {
                log.error("Message does not match the required format: " + message);
                throw new IllegalArgumentException("Message does not match the required format: " + message);
            }

            Integer entityId = jsonNode.get("entityId").asInt();
            String changeType = jsonNode.get("changeType").asText();
            String entityType = jsonNode.get("entityType").asText();
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;

            if (changeTypeEnumRepository.countByName(changeType) < 1) {
                log.error("Invalid changeType: " + changeType + ". Message: " + message);
                return;
            }
            Integer childrenId = jsonNode.has("entityId") ? jsonNode.get("entityType").asInt() : null;

            switch (entityType) {
                case "BUSINESS_CAPABILITY":
                    handleBusinessCapabilityChange(entityId, changeType, name, childrenId);
                    break;
                case "TECH_CAPABILITY":
                    handleTechCapabilityChange(entityId, changeType, name, childrenId);
                    break;
                default:
                    capabilitySubscribeService.notificationQueue(entityId, name, changeType, childrenId, entityType);
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to parse message: " + e.getMessage());
        }
    }


    private void handleBusinessCapabilityChange(Integer entityId, String changeType, String name, Integer childrenId) {
        switch (changeType) {
            case "DELETE":
                capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name, changeType, childrenId);
                break;
            case "UPDATE":
                capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name, changeType, childrenId);
                break;
            case "CREATE":
                capabilitySubscribeService.createSubscribeBusinessCapability(entityId, name, childrenId);
                break;
            default:
                log.error("Unsupported change type for BUSINESS_CAPABILITY: " + changeType);
                break;
        }
    }

    private void handleTechCapabilityChange(Integer entityId, String changeType, String name, Integer childrenId) {
        switch (changeType) {
            case "DELETE":
                capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType, childrenId);
                break;
            case "UPDATE":
                capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType, childrenId);
                break;
            case "CREATE":
                capabilitySubscribeService.createSubscribeTechCapability(entityId, name, childrenId);
                break;
        }
    }
}