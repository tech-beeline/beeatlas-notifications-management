package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public List<UnreadNotifyDTO> getNotifications(
            HttpServletRequest request,
            @RequestParam(required = false) Timestamp afterDate,
            @RequestParam(required = false) Timestamp beforeDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean wasNotify,
            @RequestParam(required = false) Integer page) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));

        return notifyService.getNotify(userId, afterDate, beforeDate, type, wasNotify, page);
    }

    @PatchMapping
    @ApiOperation(value = "Обновление статуса уведомлений")
    public ResponseEntity patchNotify(HttpServletRequest request,
                                      @RequestBody List<Integer> notifyIds) {
        Integer userId = Integer.valueOf(request.getHeader(USER_ID_HEADER));
        notifyService.patchNotify(userId, notifyIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}