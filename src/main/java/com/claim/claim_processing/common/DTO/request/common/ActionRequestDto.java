package com.claim.claim_processing.common.DTO.request.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequestDto {

    private Long id; // used for PATCH

    private String code;
    private String name;
    private Integer displayOrder;

    private String createdBy;
    private String updatedBy;
}