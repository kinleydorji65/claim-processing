package com.claim.claim_processing.common.DTO.response.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private ActivityEnum isActive;

    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
}