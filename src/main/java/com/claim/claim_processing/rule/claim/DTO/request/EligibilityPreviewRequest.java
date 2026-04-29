package com.claim.claim_processing.rule.claim.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EligibilityPreviewRequest {
    private String memberCode;
    private Long circumtancesId;
    private Long categoryId;
    private Long memberCategoryId;
    private LocalDate terminationDate;
    private Boolean isSpecialCase;
}
