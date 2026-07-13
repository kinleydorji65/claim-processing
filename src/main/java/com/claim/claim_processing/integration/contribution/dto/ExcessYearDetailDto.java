package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ExcessYearDetailDto {
    private String accountingYear;
    private String yearType;
    private BigDecimal openingBalance;
    private BigDecimal interestOnOpening;
    private BigDecimal duringTheYear;
    private BigDecimal closingBalance;
    private BigDecimal interestRate;
    private LocalDate interestDate;
    private Integer daysInYear;
    private Integer eolMonthsInYear;
    private BigDecimal yearlyContributions;
    private BigDecimal yearlyInterest;
    private List<ExcessMonthlyDetailDto> monthlyDetails;
}