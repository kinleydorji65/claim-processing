package com.claim.claim_processing.rule.claim.DTO.contribution;

import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;

import lombok.*;

@Data
@Builder
public class EligibleBenefitComponentDTO {

    private Long benefitComponentTypeId;
    private String code;
    private String name;

    private EligibilityEnum isEligible;

    private boolean selectable;

    private String ineligibilityReason;
}
