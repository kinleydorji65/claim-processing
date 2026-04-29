package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VestingRuleResponseDTO {
    
    // Basic Eligibility
    private boolean eligible;
    private String ruleCode;
    private String ruleName;
    
    // Core Result
    private String refundType;                   // "LUMPSUM", "OPTION", "PENSION"
    private String payoutResult;                 // "LUMPSUM", "PENSION_OR_LUMPSUM_OPTION", "PENSION_ONLY"
    
    // Vesting Details (for UI display)
    private String vestingStatus;                // "FULLY_VESTED", "PARTIALLY_VESTED", "NOT_VESTED"
    private Integer totalVestingMonths;
    private Integer requiredVestingMonths;
    
    // Messages
    private String message;
    private String remarks;
    
    // For UI Decision Making
    private List<String> availableOptions;       // ["LUMPSUM"] or ["PENSION", "LUMPSUM"]
    
    // Computed field (not stored, calculated on demand)
    public boolean hasOption() {
        return "OPTION".equals(refundType) || 
               "PENSION_OR_LUMPSUM_OPTION".equals(payoutResult);
    }
}