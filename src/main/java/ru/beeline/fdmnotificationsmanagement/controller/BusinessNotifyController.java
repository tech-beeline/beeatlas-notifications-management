package ru.beeline.fdmnotificationsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.beeline.fdmnotificationsmanagement.dto.BusinessNotifyDTO;
import ru.beeline.fdmnotificationsmanagement.service.NotifyService;

import java.time.LocalDateTime;

import static ru.beeline.fdmnotificationsmanagement.utils.Constant.USER_ID_HEADER;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1")
@Api(value = "business notify API", tags = "business-notify")
public class BusinessNotifyController {

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/business/notify")
    @ApiOperation(value = "Получения всех нотификаций о бизнес-событиях")
    public Page<BusinessNotifyDTO> getBusinessNotify(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime afterDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime beforeDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean wasNotify,
            @RequestParam(required = false) Integer page,
            @RequestHeader(value = USER_ID_HEADER) Integer userId) {

        return notifyService.getBusinessNotify(userId, afterDate, beforeDate, type, wasNotify, page);
    }
}
