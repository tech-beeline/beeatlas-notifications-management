package ru.beeline.fdmnotificationsmanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeTypeIdDTO {

    private Integer id;
    private String name;
    private String description;
}
