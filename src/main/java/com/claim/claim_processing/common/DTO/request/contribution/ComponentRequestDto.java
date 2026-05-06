package com.claim.claim_processing.common.DTO.request.contribution;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentRequestDto {

    private String code;
    private String name;
    private String componentType;
    private String createdBy;
    private String updatedBy;
}