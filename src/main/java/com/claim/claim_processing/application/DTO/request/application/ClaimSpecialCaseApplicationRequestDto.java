package com.claim.claim_processing.application.DTO.request.application;

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
public class ClaimSpecialCaseApplicationRequestDto {
    private Long id;

    private Long caseReasonId;

    private String approvedBy;

    private LocalDateTime approvedDate;

    private String createdBy;

    private String updatedBy;

    private List<SpecialCaseComponentBalanceDTO> componentBalances;

    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class SpecialCaseComponentBalanceDTO {
    private String code;          // PF_MC, PF_IMC, PF_EC, etc.
    private String name;          // Component display name
    private BigDecimal amount;    // Component amount
}
}