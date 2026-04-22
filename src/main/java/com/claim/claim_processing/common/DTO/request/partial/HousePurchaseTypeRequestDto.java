package com.claim.claim_processing.common.DTO.request.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousePurchaseTypeRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}