package com.claim.claim_processing.common.DTO.update.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTypeUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive;
}