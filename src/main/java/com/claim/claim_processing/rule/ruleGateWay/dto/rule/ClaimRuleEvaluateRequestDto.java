package com.claim.claim_processing.rule.ruleGateWay.dto.rule;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClaimRuleEvaluateRequestDto {

    private String nppfNumber;

    private Long claimTypeId;

    private Long cessationTypeId;

    private Long schemeTypeId;

    private LocalDate claimDate;

    private LocalDate cessationDate;

    private Long reasonId;

    private Boolean isSpecialCase;
}
