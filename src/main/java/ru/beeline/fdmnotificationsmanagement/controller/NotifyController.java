package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.fdmnotificationsmanagement.dto.EntityTypeIdDTO;
import ru.beeline.fdmnotificationsmanagement.dto.UnreadNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.service.NotifyService;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

import static ru.beeline.fdmnotificationsmanagement.utils.Constant.USER_ID_HEADER;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/notify")
@Api(value = "Capability subscribe API", tags = "capability-subscribe")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @GetMapping
    public Page<UnreadNotifyDTO> getNotifications(HttpServletRequest request,
                                                  @RequestParam(required = false) Timestamp afterDate,
                                                  @RequestParam(required = false) Timestamp beforeDate,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(required = false) Boolean wasNotify,
                                                  @RequestParam(required = false) Integer page) {

        return notifyService.getNotify(request.getIntHeader(USER_ID_HEADER), afterDate, beforeDate, type, wasNotify, page);
    }

    @PatchMapping
    @ApiOperation(value = "Обновление статуса уведомлений")
    public ResponseEntity patchNotify(HttpServletRequest request,
                                      @RequestParam(required = false, defaultValue = "all") String notifyType,
                                      @RequestBody List<Integer> notifyIds) {
        notifyService.patchNotify(request.getIntHeader(USER_ID_HEADER), notifyType, notifyIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/business-event/{entity_type}/{entity_id}")
    @ApiOperation(value = "Создания записи о бизнес-нотификации")
    public ResponseEntity businessEvent(HttpServletRequest request,
                                        @RequestParam(required = false) String name,
                                        @PathVariable(value = "entity_type") String entityType,
                                        @PathVariable(value = "entity_id") Integer entityId) {
        notifyService.postNotify(request.getIntHeader(USER_ID_HEADER), entityType, entityId, name);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/change-type")
    @ApiOperation(value = "Получение информации о типах событий")
    public List<EntityTypeIdDTO> getChangeType() {
        return notifyService.getChangeTypes();
    }

    @PostMapping("/business-event/group/role/{role}/{entity_type}/{entity_id}")
    @ApiOperation(value = "Создания записи о бизнес-нотификации для групп пользователей")
    public ResponseEntity businessEvent(@RequestParam(required = false) String name,
                                        @PathVariable String role,
                                        @PathVariable(value = "entity_type") String entityType,
                                        @PathVariable(value = "entity_id") Integer entityId) {
        notifyService.postGroupNotify(entityType, entityId, role, name);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}