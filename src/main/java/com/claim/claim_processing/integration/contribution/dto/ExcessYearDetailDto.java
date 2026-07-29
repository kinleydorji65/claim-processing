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
    private BigDecimal openingPecBalance;
    private BigDecimal openingPmcBalance;
    private BigDecimal openingPimcBalance;
    private BigDecimal openingPiecBalance;
    private BigDecimal interestOnPecOpening;
    private BigDecimal interestOnPmcOpening;
    private BigDecimal interestOnPimcOpening;
    private BigDecimal interestOnPiecOpening;
    private BigDecimal duringTheYearPmc;
    private BigDecimal duringTheYearPec;
    private BigDecimal duringTheYearPimc;
    private BigDecimal duringTheYearPiec;
    private BigDecimal closingPmcBalance;
    private BigDecimal closingPecBalance;
    private BigDecimal closingPimcBalance;
    private BigDecimal closingPiecBalance;
    private BigDecimal interestPecRate;
    private BigDecimal interestPmcRate;
    private BigDecimal interestPiecRate;
    private BigDecimal interestPimcRate;
    private LocalDate interestDate;
    private Integer yearBasis;
    private Integer daysInYear;
    private Integer eolMonthsInYear;
    private BigDecimal yearlyContributions;
    private BigDecimal yearlyInterest;
    private List<ExcessMonthlyDetailDto> monthlyDetails;  // ← Contains monthly DTOs
}