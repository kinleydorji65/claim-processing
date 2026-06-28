package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationOtherRequestDto {
    private BigDecimal finalPayableAmount;
    private Long calculationStatusId;
    private String identityNumber;
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;
    
    private Long cessationTypeId;
    private Long reasonTypeId;
    private LocalDate cessationDate;

    private String createdBy;
    private String updatedBy;
}
