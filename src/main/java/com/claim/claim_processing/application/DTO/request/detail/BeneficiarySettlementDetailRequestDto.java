package com.claim.claim_processing.application.DTO.request.detail;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiarySettlementDetailRequestDto {

    private List<Long> beneficiaryClaimantDetailIds;

    private LocalDate dateOfDeath;

    private LocalDate serviceJoiningDate;

    private LocalDate lastContributionDate;

    private Integer nonContributionMonths;

    /**
     * Audit
     */
    private String createdBy;

    private String updatedBy;
    private List<BeneficiaryClaimantRequestDto> beneficiaryClaimants;
}