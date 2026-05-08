package com.claim.claim_processing.common.DTO.response.wrongRemittance;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceErrorTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}