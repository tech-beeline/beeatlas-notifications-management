package ru.beeline.fdmnotificationsmanagement.dto;

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
    private Timestamp changeDate;
    private Integer entityId;
    private String changeType;
    private String entityName;
    private String entityLink;
    private String entityType;
}
