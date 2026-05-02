package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ClaimCalculationResponseDTO {
    private String memberCode;
    private String memberName;
    private String cidNo;
    
    // Service period
    private LocalDate contributionStartDate;
    private LocalDate contributionEndDate;
    private LocalDate cessationDate;
    private LocalDate lastInterestCalculationDate;
    
    // Component balances (raw components from Table 1)
    private List<ComponentBalanceDTO> components;
    
    // Summary
    private LocalDate asOfDate;

    @Data
@Builder
public static class ComponentBalanceDTO {
    private String code;        // PF_MC, PF_IMC, PF_EC, etc.
    private String name;
    private String type;        // CONTRIBUTION or INTEREST
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDate lastUpdatedDate;
}

}

