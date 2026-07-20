package com.claim.claim_processing.application.DTO.request.workFlow;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApprovalRequestDto {
    private Long claimApplicationId;
    private String remarks;
    private String approvedBy;
}