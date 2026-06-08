package com.claim.claim_processing.application.DTO.response.detail;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarySettlementResponseDto {

    private Long id;

    // Parent
    private Long claimApplicationId;
    private String applicationNumber;

    // Claimants
    private List<BeneficiaryClaimantResponseDto> beneficiaryClaimantDetails;

    // Master
    private Long cessationTypeId;
    private String cessationTypeName;

    // Dates
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfDeath;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastContributionDate;

    // Audit
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}