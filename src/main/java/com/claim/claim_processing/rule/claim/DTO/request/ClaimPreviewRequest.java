package com.claim.claim_processing.rule.claim.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimPreviewRequest {
    private String memberCode;
    private Long circumtancesId;
    private String memberCategoryId;
    private LocalDate terminationDate;
    private LocalDate cessationDate;
    private LocalDate serviceJoiningDate; 
    private Boolean isSpecialCase;
}
