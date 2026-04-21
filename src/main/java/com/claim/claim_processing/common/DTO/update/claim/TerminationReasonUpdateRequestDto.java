package com.claim.claim_processing.common.DTO.update.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationReasonUpdateRequestDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private Character isActive;
}