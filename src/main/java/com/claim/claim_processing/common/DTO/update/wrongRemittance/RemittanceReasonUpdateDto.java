package com.claim.claim_processing.common.DTO.update.wrongRemittance;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceReasonUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive;
}