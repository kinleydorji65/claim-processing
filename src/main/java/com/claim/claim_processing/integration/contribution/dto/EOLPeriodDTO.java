package com.claim.claim_processing.integration.contribution.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EOLPeriodDTO {
    private String period;        // e.g., "Jul 2020 - Jun 2021"
    private int eolMonths;        // e.g., 3
    private String startDate;     // e.g., "2020-07-15"
    private String endDate;       // e.g., "2021-06-30"
    private String ruleType;      // e.g., "Financial Year (Jul-Jun)" or "Calendar Year (Jan-Dec)"
}
