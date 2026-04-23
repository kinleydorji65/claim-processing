package com.claim.claim_processing.common.DTO.request.wrong_remittance;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceReasonRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}