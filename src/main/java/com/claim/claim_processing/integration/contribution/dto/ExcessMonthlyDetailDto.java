package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)  // ✅ Force include all fields
public class ExcessMonthlyDetailDto {
    private String dueMonth;
    private LocalDate invoiceDate;
    private BigDecimal pmc;
    private BigDecimal pec;
    private BigDecimal pimc;
    private BigDecimal piec;
    private BigDecimal totalPension;
    private Integer days;
    private Integer eolNumber;
    
    @JsonProperty("interestRate")  // ✅ Explicit JSON property
    private BigDecimal interestRate;
    
    @JsonProperty("yearBasis")     // ✅ Explicit JSON property
    private Integer yearBasis;
}