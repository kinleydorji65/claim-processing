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

    private Long id;

    /**
     * Claim Application Reference
     */
    private Long claimApplicationId;

    /**
     * Beneficiary Claimants
     */
    private List<Long> beneficiaryClaimantDetailIds;

    /**
     * Service / Membership Details
     */
    private LocalDate pfJoiningDate;

    private LocalDate pensionJoiningDate;

    private LocalDate dateOfDeath;

    private LocalDate serviceJoiningDate;

    private LocalDate lastContributionDate;

    private Integer nonContributionMonths;

    /**
     * Usually DEATH
     */
    private Long cessationTypeId;

    /**
     * Deceased Member Details
     */
    private String deceasedMemberCode;

    private String deceasedNppfNumber;

    /**
     * Audit
     */
    private String createdBy;

    private String updatedBy;
}