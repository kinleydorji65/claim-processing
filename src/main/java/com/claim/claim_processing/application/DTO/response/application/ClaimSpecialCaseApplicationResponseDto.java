package com.claim.claim_processing.application.DTO.response.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplicationResponseDto {

    private Long id;

    // Reference to the claim
    private Long claimApplicationId;
    private String applicationNumber;

    private Long caseReasonId;
    private String caseReasonName;

    private BigDecimal totalAmount;
    private String approvedBy;
    private LocalDateTime approvedDate;

    // Components
    private List<SpecialCaseComponentBalanceResponseDTO> components;

    // Audit Information
    private String isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialCaseComponentBalanceResponseDTO {
        private Long id;
        private String code; // PF_MC, PF_IMC, PF_EC, etc.
        private String name; // Component display name
        private BigDecimal amount; // Component amount
    }
}