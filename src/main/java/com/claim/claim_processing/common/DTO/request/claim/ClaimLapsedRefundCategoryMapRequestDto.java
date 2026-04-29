package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundCategoryMapRequestDto {

    private Long id; // optional for update

    private Long ruleId;

    private String categoryId;
}