package com.claim.claim_processing.rule.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimInitialPreviewRequest {
    private Long claimTypeId;
    private Long cessationTypeId;
    private String nppfNumber;
    private String memberCategoryId;
    private LocalDate cessationDate;
    private LocalDate serviceJoiningDate; 
    private Boolean isSpecialCase;
    private Long reasonTypeId;
}
