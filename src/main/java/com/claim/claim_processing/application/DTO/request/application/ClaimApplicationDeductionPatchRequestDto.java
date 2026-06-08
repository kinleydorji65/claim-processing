package com.claim.claim_processing.application.DTO.request.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionPatchRequestDto {
    private Long deductionDetailId;
    private BigDecimal verifiedDeductedAmount;

    private BigDecimal approvedDeductedAmount;

    private BigDecimal deductedAmount;

    private Long deductionReviewStatusId;

    private ActivityEnum isManualOverride;

    private String overrideReason;

    private String remarks;

    private String updatedBy;

    private List<DeductionItemPatchDto> deductionItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeductionItemPatchDto {

        private Long deductionItemId;

        private BigDecimal deductedAmount;

        private BigDecimal remainingAmount;

        private String remarks;
    }
}
