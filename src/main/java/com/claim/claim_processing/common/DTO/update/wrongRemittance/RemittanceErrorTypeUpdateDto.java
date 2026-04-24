package com.claim.claim_processing.common.DTO.update.wrongRemittance;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceErrorTypeUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive; // "Y" / "N"
}