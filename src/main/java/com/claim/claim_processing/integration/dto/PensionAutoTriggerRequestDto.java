package com.claim.claim_processing.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensionAutoTriggerRequestDto {
    private String memberCode;
    private String agencyCode;
    private String pensionType;
    private LocalDate exitDate;
    private String exitReason;
    private Long pfSettlementClaimId;
    private String remarks;
    private BigDecimal finalBasicSalary;
    private LocalDate dateOfServiceJoining;
    private Long bankTypeId;
    private String bankName;
    private String bankAccountNumber;
    private String accountHolderName;
    private String ifscOrRoutingCode;

    /** Full contribution-component breakdown from the claim's calculation (both PF_ and P_ codes) —
     *  pension-service decides which of these it actually needs to keep. */
    private List<ComponentDto> components;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDto {
        private Long id;
        private String code;
        private String name;
        private BigDecimal amount;
    }
}