package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseResponse {
    private Long id;
    private Long claimDetailId;

    private Long caseReasonId;

    // Approval Information
    private String approvedBy;

    private LocalDateTime approvedDate;

    private BigDecimal totalAmount;
    // Audit Information
    private String isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    private List<SpecialCaseComponentBalanceResponseDTO> components;

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
