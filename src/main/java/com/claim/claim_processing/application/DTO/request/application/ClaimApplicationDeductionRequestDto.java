package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionRequestDto {
    private Long deductionDetailId;
    private BigDecimal outstandingAmount;
    private BigDecimal verifiedDeductedAmount;
    private BigDecimal approvedDeductedAmount;
    private BigDecimal deductedAmount;
    private String remarks;
    private String createdBy;
    private String updatedBy;

    private List<DeductionItemDto> deductionItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeductionItemDto {
        private Long deductionItemId;
        private String deductionCategory; // LOAN / RENTAL / TAX / OTHER
        private BigDecimal outstandingAmount;
        private BigDecimal deductedAmount;
        private BigDecimal remainingAmount;
        private String remarks;
        private String createdBy;
        private String updatedBy;
    }
}
