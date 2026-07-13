package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExcessMonthlyDetailDto {
    private String dueMonth;
    private LocalDate invoiceDate;
    private BigDecimal mpc;
    private BigDecimal epc;
    private BigDecimal totalPension;
    private Integer days;
    private BigDecimal interest;
    private BigDecimal cPlusI;
    private boolean isEOL;
}