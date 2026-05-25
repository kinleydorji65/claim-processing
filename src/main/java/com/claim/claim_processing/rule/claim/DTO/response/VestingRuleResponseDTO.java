package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.integration.contribution.dto.EligibleBenefitComponentDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VestingRuleResponseDTO {
    private String ruleCode;
    private List<String> eligibilityNote;
    
    // Core Result
    private List<VestingRefundTypeResponseDto> refundType;                   // "LUMPSUM", "OPTION", "PENSION"
    private String payoutResult;                 // "LUMPSUM", "PENSION_OR_LUMPSUM_OPTION", "PENSION_ONLY"
    
    private Integer totalVestingMonths;
    private Integer requiredVestingMonths;
    private List<EligibleBenefitComponentDTO> categoryBenefits;   // For category-based rules
}