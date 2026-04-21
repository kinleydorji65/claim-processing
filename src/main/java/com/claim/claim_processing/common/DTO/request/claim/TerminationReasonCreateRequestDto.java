package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationReasonCreateRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}
