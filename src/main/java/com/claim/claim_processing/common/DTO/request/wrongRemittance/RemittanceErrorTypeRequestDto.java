package com.claim.claim_processing.common.DTO.request.wrongRemittance;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceErrorTypeRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}