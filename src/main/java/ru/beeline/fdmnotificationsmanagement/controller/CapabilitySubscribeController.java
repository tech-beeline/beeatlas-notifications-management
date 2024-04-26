package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilitySubscribeDto;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;

import java.util.List;

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

    @GetMapping("/business-capability-subscribe/{id}")
    @ApiOperation(value = "Статус подписки на business-capability", response = List.class)
    public ResponseEntity<CapabilitySubscribeDto> checkBusinessCapabilitySubscribeById(@PathVariable(value = "id") Integer idSubscribe) {
        return ResponseEntity.status(HttpStatus.OK).body(new CapabilitySubscribeDto(businessInteractionService.checkBusinessCapabilitySubscribeById(idSubscribe)));
    }

    @GetMapping("/business-capability-children-subscribe/{id}")
    @ApiOperation(value = "Статус подписки на business-capability-children", response = List.class)
    public ResponseEntity<CapabilitySubscribeDto> checkBusinessCapabilityChildrenSubscribeById(@PathVariable(value = "id") String idSubscribe) {
        return ResponseEntity.status(HttpStatus.OK).body(new CapabilitySubscribeDto(businessInteractionService.checkBusinessCapabilityChildrenSubscribeById(idSubscribe)));
    }
}