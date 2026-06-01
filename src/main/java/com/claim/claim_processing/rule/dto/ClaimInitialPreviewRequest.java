package com.claim.claim_processing.rule.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimInitialPreviewRequest {

    private Long claimTypeId;
    private Long cessationTypeId;
    private String nppfNumber;
    private Long reasonTypeId;

    private LocalDate cessationDate;
    private Boolean isSpecialCase;
}
