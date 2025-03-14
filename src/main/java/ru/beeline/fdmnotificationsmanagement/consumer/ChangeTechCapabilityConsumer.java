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