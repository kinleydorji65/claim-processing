package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ExcessServiceResultDto {
    private boolean isEligible;
    private BigDecimal totalExcessAmount;
    private LocalDate cutoffServiceDate;
    private Integer cutoffYears;
    private LocalDate excessStartDate;
    private LocalDate excessEndDate;
    private Integer totalEOLMonths;
    private Integer eolMonthsInExcess;
    private BigDecimal totalContributionsInExcess;
    private BigDecimal totalInterestInExcess;
    private List<ExcessYearDetailDto> yearDetails;
    private List<ExcessMonthlyDetailDto> monthlyDetails;
    private String status;
    private String message;
}
