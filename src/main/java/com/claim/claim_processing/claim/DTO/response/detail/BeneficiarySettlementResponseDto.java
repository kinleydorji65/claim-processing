package com.claim.claim_processing.claim.DTO.response.detail;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarySettlementResponseDto {

    private Long id;

    // ---------- Parent ----------
    private Long claimApplicationId;

    private List<BeneficiaryClaimantResponseDto> beneficiaryClaimantDetails;  

    // ---------- Master ----------
    private Long cessationTypeId;
    private String cessationTypeName;

    // ---------- Dates ----------
    private LocalDate pfJoiningDate;
    private LocalDate pensionJoiningDate;

    private LocalDate dateOfDeath;
    private LocalDate serviceJoiningDate;
    private LocalDate lastContributionDate;

    // ---------- Business ----------
    private Integer nonContributionMonths;

    // ---------- Audit ----------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
