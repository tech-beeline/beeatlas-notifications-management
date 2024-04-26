package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.fdmnotificationsmanagement.domain.EntityTypeEnum;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilitySubscribeDto;
import ru.beeline.fdmnotificationsmanagement.dto.SubscriptionDTO;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.beeline.fdmnotificationsmanagement.utils.Constant.USER_ID_HEADER;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Api(value = "Capability subscribe API", tags = "capability-subscribe")
public class CapabilitySubscribeController {

    @Autowired
    private CapabilitySubscribeService businessInteractionService;

    @GetMapping("/all-entity-subscribe/{userId}")
    @ApiOperation(value = "Получить все подписки пользователя", response = List.class)
    public ResponseEntity<List<Integer>> getAllEntitySubscribeByUserId(@RequestParam(value = "entity-type ") String entityType ,
                                                                                   @PathVariable(value = "userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(businessInteractionService.getAllEntitySubscribeByUserId(userId, entityType));
    }

    @GetMapping("/tech-capability-subscribe/{id}")
    @ApiOperation(value = "Статус подписки на tech-capability", response = List.class)
    public ResponseEntity<CapabilitySubscribeDto> checkTechCapabilitySubscribeById(@PathVariable(value = "id") Integer idSubscribe) {
        return ResponseEntity.status(HttpStatus.OK).body(new CapabilitySubscribeDto(businessInteractionService.checkTechCapabilitySubscribeById(idSubscribe)));
    }

    @PutMapping("/tech-capability-subscribe/{id}")
    @ApiOperation(value = "Подписка на tech-capability", response = SubscriptionDTO.class)
    public ResponseEntity<SubscriptionDTO> subscribeToTechCapabilityById(@PathVariable(value = "id") Integer entityId, HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        return ResponseEntity.status(HttpStatus.OK).body(new SubscriptionDTO(businessInteractionService.findOrCreateSubscription(EntityTypeEnum.CapabilitySubscriptionType.TECH, entityId, userId)));
    }

    @GetMapping("/business-capability-subscribe/{id}")
    @ApiOperation(value = "Статус подписки на business-capability", response = List.class)
    public ResponseEntity<CapabilitySubscribeDto> checkBusinessCapabilitySubscribeById(@PathVariable(value = "id") Integer idSubscribe) {
        return ResponseEntity.status(HttpStatus.OK).body(new CapabilitySubscribeDto(businessInteractionService.checkBusinessCapabilitySubscribeById(idSubscribe)));
    }

    @PutMapping("/business-capability-subscribe/{id}")
    @ApiOperation(value = "Подписка на  business-capability", response = SubscriptionDTO.class)
    public ResponseEntity<SubscriptionDTO> subscribeToBusinessCapabilityById(@PathVariable(value = "id") Integer entityId, HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        return ResponseEntity.status(HttpStatus.OK).body(new SubscriptionDTO(businessInteractionService.findOrCreateSubscription(EntityTypeEnum.CapabilitySubscriptionType.BUSINESS, entityId, userId)));
    }

    @GetMapping("/business-capability-children-subscribe/{id}")
    @ApiOperation(value = "Статус подписки на business-capability-children", response = List.class)
    public ResponseEntity<CapabilitySubscribeDto> checkBusinessCapabilityChildrenSubscribeById(@PathVariable(value = "id") String idSubscribe) {
        return ResponseEntity.status(HttpStatus.OK).body(new CapabilitySubscribeDto(businessInteractionService.checkBusinessCapabilityChildrenSubscribeById(idSubscribe)));
    }

    @PutMapping("/business-capability-children-subscribe/{id}")
    @ApiOperation(value = "Подписка на  business-capability и всех потомков", response = List.class)
    public ResponseEntity<SubscriptionDTO> subscribeToBusinessCapabilityChildrenById(@PathVariable(value = "id") Integer entityId, HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        return ResponseEntity.status(HttpStatus.OK).body(new SubscriptionDTO(businessInteractionService.findOrCreateSubscription(EntityTypeEnum.CapabilitySubscriptionType.BUSINESS_WITH_CHILDREN, entityId, userId)));
    }
}