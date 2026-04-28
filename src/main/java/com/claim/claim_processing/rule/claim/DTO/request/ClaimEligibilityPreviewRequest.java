package com.claim.claim_processing.rule.claim.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimEligibilityPreviewRequest {
    private String memberCode;
    private Long cessationTypeId;
    private Long circumtancesId;
    private Long categoryId;
    private Long memberCategoryId;
    private LocalDate terminationDate;
    private Boolean isSpecialCase;
}
