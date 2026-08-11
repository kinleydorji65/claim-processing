package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ExcessServiceResultDto {
    // ================================================================
    // ELIGIBILITY
    // ================================================================
    private boolean isEligible;
    private String status;
    private String message;

    // ================================================================
    // CUTOFF INFORMATION
    // ================================================================
    private LocalDate cutoffServiceDate;
    private Integer cutoffYears;
    private LocalDate excessStartDate;
    private LocalDate excessEndDate;

    // ================================================================
    // EOL BREAKDOWN
    // ================================================================
    private Integer totalEOLMonths;              // Total EOL months across all periods
    private Integer eolMonthsBeforeCutoffYear;   // EOL months BEFORE cutoff year (added to cutoff)
    private Integer eolMonthsDuringCutoffYear;   // EOL months DURING cutoff year (NOT deducted)
    private Integer eolMonthsAfterCutoffYear;    // EOL months AFTER cutoff year (deducted from excess)
    private Integer eolMonthsInExcess;           // EOL months during excess period
    private Long monthsShort;                    // Months short if not eligible

    // ================================================================
    // FINANCIAL SUMMARY
    // ================================================================
    private BigDecimal totalExcessAmount;
    private BigDecimal totalContributionsInExcess;
    private BigDecimal totalInterestInExcess;

    // ================================================================
    // DETAILS
    // ================================================================
    private List<ExcessYearDetailDto> yearDetails;
    private List<ExcessMonthlyDetailDto> monthlyDetails;
    private List<EOLPeriodDTO> eolPeriods;       // All EOL periods found
    private CategoryInfoDto categoryInfo;       // All EOL periods found

    // ================================================================
    // ADDITIONAL METADATA
    // ================================================================
    private LocalDate calculationDate;
    private String calculationMethod;            // e.g., "STANDARD_WITH_EOL_ADJUSTMENT"

    // ================================================================
    // CATEGORY INFORMATION
    // ================================================================
    
    /** Member category type (CIVIL, SECURITY_FORCES, PENSION_INELIGIBLE) */
    private String categoryType;
    
    /** Member category ID (01, 03, 04) */
    private String memberCategoryId;
    
    /** Category display name (Civil, Security Forces, etc.) */
    private String categoryDisplayName;
    
    /** Flag indicating if member is in Security Forces (Category "03") */
    private boolean isSecurityForces;
    
    /** Flag indicating if Security Forces should follow Civil rules */
    private boolean followCivilRules;
    
    /** Flag indicating if pension should be retained (Security Forces rule) */
    private boolean pensionRetained;
    
    /** Reason for pension retention decision */
    private String pensionRetainedReason;
    
    /** Cutoff date used for Security Forces rule (July 1, 2024) */
    private LocalDate securityForcesCutoffDate;
}