package com.claim.claim_processing.common.DTO.request.calculationMaster;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationTriggerTypeRequestDto {

    private Long id;

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;

    private String updatedBy;
    private String createdBy;
}