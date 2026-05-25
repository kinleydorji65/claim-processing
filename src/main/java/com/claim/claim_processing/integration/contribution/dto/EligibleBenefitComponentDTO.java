package com.claim.claim_processing.integration.contribution.dto;

import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;

import lombok.*;

@Data
@Builder
public class EligibleBenefitComponentDTO {
    private String code;
    private String benifitComponentName;

    private EligibilityEnum isPensionEligible;

    private boolean selectable;
}
