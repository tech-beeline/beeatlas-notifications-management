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

    @RabbitListener(queues = "${queue.change-tech-capability.name}")
    public void changeTechCapabilityQueue(String message) {
        log.info("Received from change-tech-capability: " + message, new String(message.getBytes()));
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.has("entity_id") && jsonNode.has("change_type")) {
                String changeType = jsonNode.get("change_type").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeTechCapability(entityId);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribe(entityId);
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
            if (jsonNode.has("entity_id") && jsonNode.has("change_type")) {
                String changeType = jsonNode.get("change_type").asText();
                Integer entityId = jsonNode.get("entity_id").asInt();

                switch (changeType) {
                    case "UPDATE":
                        capabilitySubscribeService.updateSubscribeBusinessCapability(entityId);
                        break;
                    case "CREATE":
                        capabilitySubscribeService.createSubscribe(entityId);
                        break;
                }
            } else {
                log.error("Message does not match the required format");
            }
        } catch (Exception e) {
            log.error("Internal server Error: " + e.getMessage());
        }
    }
}