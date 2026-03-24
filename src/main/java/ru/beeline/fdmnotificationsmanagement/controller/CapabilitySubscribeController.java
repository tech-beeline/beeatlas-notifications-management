/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.fdmnotificationsmanagement.dto.GetUserSubscribeDTO;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.beeline.fdmnotificationsmanagement.utils.Constant.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Api(value = "Capability subscribe API", tags = "capability-subscribe")
public class CapabilitySubscribeController {

    @Autowired
    private CapabilitySubscribeService capabilityInteractionService;

    @GetMapping("/subscribe/{entityType}")
    @ApiOperation(value = "Получение подписок по типу сущности")
    public ResponseEntity<List<Integer>> getSubscribesByEntityType(@PathVariable(value = "entityType") String entityType,
                                                                   HttpServletRequest request) {
        List<Integer> ids = capabilityInteractionService.getAllEntitySubscribeByUserIdAndEntityType(request.getIntHeader(USER_ID_HEADER), entityType);
        log.info("result subscribes ids: " + ids);
        return ResponseEntity.status(HttpStatus.OK).body(ids);
    }

    @GetMapping("/subscribe")
    @ApiOperation(value = "Получение подписок пользователя")
    public ResponseEntity<List<GetUserSubscribeDTO>> gerUserSubscribes(
            @RequestParam(value = "entityType", required = false) String entityType,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                capabilityInteractionService.getUserSubscribes(entityType, request.getIntHeader(USER_ID_HEADER)));
    }

    @PostMapping("/subscribe/{entityType}/{id}")
    @ApiOperation(value = "Добавление подписки")
    public ResponseEntity addSubscribe(@PathVariable(value = "entityType") String entityType,
                                       @PathVariable(value = "id") Integer entityId,
                                       HttpServletRequest request,
                                       @RequestParam(value = "sub-children", required = false) boolean subChildren,
                                       @RequestParam(value = "name", required = false) String name) {
        capabilityInteractionService.addSubscribe(entityId, request.getIntHeader(USER_ID_HEADER), entityType,
                subChildren, name);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/subscribe/{entityType}/{id}")
    @ApiOperation(value = "Удаление подписки")
    public ResponseEntity delete(@PathVariable(value = "entityType") String entityType, @PathVariable(value = "id") Integer entityId,
                                 HttpServletRequest request) {
        capabilityInteractionService.deleteSubscribe(entityId, request.getIntHeader(USER_ID_HEADER), entityType);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}