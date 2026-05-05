package com.claim.claim_processing.common.DTO.request.contribution;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitComponentDetailRequestDto {

    private Long id;

    private Long benefitComponentTypeId;

    private Long componentId;
}