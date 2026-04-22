package com.claim.claim_processing.common.DTO.request.legal_master;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryReasonRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}