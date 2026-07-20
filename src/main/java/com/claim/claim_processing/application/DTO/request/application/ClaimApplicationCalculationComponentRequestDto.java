package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationComponentRequestDto {
    private Long calculationComponentId;
    private String componentCode;
    private BigDecimal amount;
    private String notes;
    private String createdBy;
    private String updatedBy;
}
