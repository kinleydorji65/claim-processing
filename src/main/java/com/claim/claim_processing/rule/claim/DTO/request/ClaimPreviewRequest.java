package com.claim.claim_processing.rule.claim.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimPreviewRequest {
    private Long claimTypeId;
    private String memberCode;
    private Long circumtancesId;
    private String memberCategoryId;
    private LocalDate cessationDate;
    private LocalDate serviceJoiningDate; 
    private Boolean isSpecialCase;
}
