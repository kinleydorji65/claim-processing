package com.claim.claim_processing.common.DTO.update.legalMaster;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryReasonUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive;
}