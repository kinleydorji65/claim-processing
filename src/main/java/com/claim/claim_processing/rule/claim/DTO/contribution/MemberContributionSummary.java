package com.claim.claim_processing.rule.claim.DTO.contribution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberContributionSummary {
    private String memberCode;
    private Long schemeTypeId;
    
    // Service period
    private Integer totalContributionMonths;
    private Integer totalContributionYears;
    private LocalDate contributionStartDate;
    private LocalDate contributionEndDate;
    private LocalDate cessationDate;
    private LocalDate balanceAsOfDate;
    
    // Component balances (principal + interest grouped)
    private List<ComponentGroup> componentGroups;
    
    // Total balance (sum of all components)
    private BigDecimal totalBalance;
    
    @Data
    @Builder
    public static class ComponentGroup {
        private String code;        // PF_MC, PF_EC, PC_MC, etc.
    private String name;
    private String groupType;   // EMPLOYEE, EMPLOYER, VOLUNTARY, PENSION
    
    // Principal amount (no interest)
    private BigDecimal principal;
    
    // Interest amount (separate component)
    private BigDecimal interest;
    private String interestComponentCode;  // PF_IMC, PF_IEC, etc.
    
    // Total = Principal + Interest
    private BigDecimal totalBalance;
    
    // Metadata
    private BigDecimal interestRate;
    private LocalDate lastInterestDate;
    private LocalDate lastUpdatedDate;
    }
}