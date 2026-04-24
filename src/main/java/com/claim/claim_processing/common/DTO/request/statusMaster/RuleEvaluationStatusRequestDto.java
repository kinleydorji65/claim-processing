package com.claim.claim_processing.common.DTO.request.statusMaster;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleEvaluationStatusRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}