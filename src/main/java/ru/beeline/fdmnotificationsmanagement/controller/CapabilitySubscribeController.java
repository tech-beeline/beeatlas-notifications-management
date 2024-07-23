package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.fdmnotificationsmanagement.service.CapabilitySubscribeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.beeline.fdmnotificationsmanagement.utils.Constant.USER_ID_HEADER;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        return ResponseEntity.status(HttpStatus.OK).body(capabilityInteractionService.getAllEntitySubscribeByUserIdAndEntityType(userId, entityType));
    }

    @PostMapping("/subscribe/{entityType}/{id}")
    @ApiOperation(value = "Добавление подписки")
    public ResponseEntity addSubscribe(@PathVariable(value = "entityType") String entityType,
                                       @PathVariable(value = "id") Integer entityId,
                                       HttpServletRequest request,
                                       @RequestParam(value = "sub-children", required = false) boolean subChildren) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        capabilityInteractionService.addSubscribe(entityId, userId, entityType, subChildren);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/notify")
    @ApiOperation(value = "Обновление статуса уведомлений")
    public ResponseEntity patchNotify(HttpServletRequest request,
                                      @RequestParam List<Integer> notifyIds) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        capabilityInteractionService.patchNotify(userId, notifyIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/subscribe/{entityType}/{id}")
    @ApiOperation(value = "Удаление подписки")
    public ResponseEntity delete(@PathVariable(value = "entityType") String entityType, @PathVariable(value = "id") Integer entityId, HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        capabilityInteractionService.deleteSubscribe(entityId, userId, entityType);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}