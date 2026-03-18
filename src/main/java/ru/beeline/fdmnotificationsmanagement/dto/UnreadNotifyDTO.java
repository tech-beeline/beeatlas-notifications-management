/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnreadNotifyDTO {
    private Integer id;
    private Boolean webNotify;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp changeDate;
    private Integer entityId;
    private String changeType;
    private String entityName;
    private String entityLink;
    private String entityType;
    private String alias;
    private Integer childrenEntityId;
    private String linkTemplate;
    private String changeDescription;
}
