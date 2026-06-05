package com.claim.claim_processing.rule.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimInitialPreviewRequest {

    private Long claimTypeId;
    private Long cessationTypeId;
    private String nppfNumber;
    private Long reasonTypeId;

    private LocalDate cessationDate;
    private Boolean isSpecialCase;
}
