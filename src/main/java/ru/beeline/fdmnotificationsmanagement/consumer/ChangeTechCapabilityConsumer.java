package ru.beeline.fdmnotificationsmanagement.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;


@Slf4j
@Component
@EnableRabbit
public class ChangeTechCapabilityConsumer {

    @Autowired
    CapabilitySubscribeService capabilitySubscribeService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${queue.tech-queue.name}")
    public void techQueue(String message) {
        log.info("Received from tech_queue: " + message, new String(message.getBytes()));
        try {
            JsonNode jsonArray = objectMapper.readTree(message);
            if (jsonArray.isArray()) {
                for (JsonNode jsonNode : jsonArray) {
                    if (jsonNode.has("entity_id")
                            && jsonNode.has("change_type")
                            && jsonNode.has("name")) {
                        capabilitySubscribeService.techQueueProcessor(
                                jsonNode.get("entity_id").asInt(),
                                jsonNode.get("name").asText(),
                                jsonNode.get("change_type").asText()
                        );
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
            if (jsonNode.has("entity_id")
                    && jsonNode.has("change_type")
                    && jsonNode.has("name")) {
                String changeType = jsonNode.get("change_type").asText();
                String name = jsonNode.get("name").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribeTechCapability(entityId, name);
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
            if (jsonNode.has("entity_id")
                    && jsonNode.has("change_type")
                    && jsonNode.has("name")) {
                String changeType = jsonNode.get("change_type").asText();
                String name = jsonNode.get("name").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name, changeType);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribeBusinessCapability(entityId, name);
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
        } catch (Exception e) {
            log.error("Failed to parse message: " + e.getMessage());
            return; // Удаляем сообщение из очереди без обработки
        }

        Integer entityId = jsonNode.get("entityId").asInt();
        String changeType = jsonNode.get("changeType").asText();
        String entityType = jsonNode.get("entityType").asText();
        String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";

        switch (entityType) {
            case "BUSINESS_CAPABILITY":
                handleBusinessCapabilityChange(entityId, changeType, name);
                break;
            case "TECH_CAPABILITY":
                handleTechCapabilityChange(entityId, changeType, name);
                break;
            default:
                capabilitySubscribeService.techQueueProcessor(entityId, name, changeType);
                break;
        }
    }

    private void handleBusinessCapabilityChange(Integer entityId, String changeType, String name) {
        switch (changeType) {
            case "DELETE":
                capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name,changeType);
                break;
            case "UPDATE":
                capabilitySubscribeService.updateSubscribeBusinessCapability(entityId, name,changeType);
                break;
            case "CREATE":
                capabilitySubscribeService.createSubscribeBusinessCapability(entityId, name);
                break;
            default:
                log.error("Unsupported change type for BUSINESS_CAPABILITY: " + changeType);
                break;
        }
    }

    private void handleTechCapabilityChange(Integer entityId, String changeType, String name) {
        switch (changeType) {
            case "DELETE":
                capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType);
                break;
            case "UPDATE":
                capabilitySubscribeService.updateSubscribeTechCapability(entityId, name, changeType);
                break;
            case "CREATE":
                capabilitySubscribeService.createSubscribeTechCapability(entityId, name);
                break;
        }
    }
}