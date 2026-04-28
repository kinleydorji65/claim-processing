package com.claim.claim_processing.rule.claim.DTO.result;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RuleEvaluationResultDto {

    private ClaimEligibilityResponseDto rule;

    private boolean eligible;

    private List<String> failedChecks;
}
