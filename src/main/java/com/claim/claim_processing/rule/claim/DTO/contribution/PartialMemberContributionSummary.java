package com.claim.claim_processing.rule.claim.DTO.contribution;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Data
@Builder
public class PartialMemberContributionSummary {
    private BigDecimal totalBalance;
    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private BigDecimal totalPf;
    private BigDecimal totalPension;
    private LocalDate contributionStartDate;
    private LocalDate contributionEnDate;
}
