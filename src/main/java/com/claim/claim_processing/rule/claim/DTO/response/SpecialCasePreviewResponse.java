package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse.NormalClaimDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialCasePreviewResponse {
    private String caseType;
    
    // Case Type Specific Calculation Preview
    private BenefitCalculationPreview calculationPreview;

    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BenefitCalculationPreview {
        // For CONVERSION_FROM_PENSION_TO_LUMSUM
        private PensionToLumpSumConversion pensionToLumpSum;
        // For CLAIM_FORFEITED_COMPONENT
        private ForfeitedComponentClaim forfeitedComponentClaim;
        
        private NormalClaimDto normalClaimDto;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PensionToLumpSumConversion {
        private Integer totalContributionMonths;
        private Long pensionDetailId;
        private String pensionType;
        private LocalDate pensionStartDate;
        private Integer totalContributionYears;
        private BigDecimal totalPensionAmount;
        private Long bankTypeId;
        private String bankName;
        private String identityNumber;
        private String accountHolderName;
        private String bankAccountNumber;
        private String ifscCode;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForfeitedComponentClaim {
        private BigDecimal totalForfeitedAmount;
        private BigDecimal eligibleClaimAmount;
        private Long reserveAccountId;
        private LocalDateTime forfeitedDate;
        private String componentCodes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NormalClaimDto {
        private Integer totalContributionMonths;
        private Integer totalNonContributionMonths;
        private BigDecimal totalPensionAmount;
        private BigDecimal totalPfAmount;
        private List<ComponentDto> Components;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDto {
        private String component;
        private String componentAmount;
    }
}
