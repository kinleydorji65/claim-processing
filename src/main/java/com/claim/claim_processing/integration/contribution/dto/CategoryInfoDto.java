package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for Category Information in Excess Service Calculation
 * 
 * This DTO holds all category-related information for a member,
 * including category type, security forces flag, and pension retention rules.
 * 
 * @author PPFMS Team
 * @version 1.0
 */
@Data
@Builder
public class CategoryInfoDto {

    // ================================================================
    // CATEGORY IDENTIFICATION
    // ================================================================
    
    /**
     * Member category ID
     * - "01" = Civil
     * - "03" = Security Forces (Armed Forces, Police, Royal Body Guard)
     * - "04" = Pension Ineligible
     */
    private String categoryId;
    
    /**
     * Category type
     * - CIVIL
     * - SECURITY_FORCES
     * - PENSION_INELIGIBLE
     */
    private String categoryType;
    
    /**
     * Display name for the category
     * - "Civil"
     * - "Security Forces (Armed Forces/Police/Royal Body Guard)"
     * - "Pension Ineligible"
     */
    private String displayName;

    // ================================================================
    // SECURITY FORCES SPECIFIC FLAGS
    // ================================================================
    
    /**
     * Flag indicating if member belongs to Security Forces (Category "03")
     * true = Member is in Security Forces
     * false = Member is not in Security Forces
     */
    private boolean isSecurityForces;
    
    /**
     * Flag indicating if Security Forces should follow Civil rules
     * true = Follow Civil rules (excess started BEFORE July 1, 2024)
     * false = Follow Security Forces rules (excess started ON or AFTER July 1, 2024)
     */
    private boolean followCivilRules;
    
    /**
     * Flag indicating if pension should be retained
     * true = Pension MUST BE RETAINED (Security Forces + excess on/after July 1, 2024)
     * false = Pension CAN BE TAKEN
     */
    private boolean pensionRetained;
    
    /**
     * Reason for the pension retention decision
     * Provides detailed explanation of why pension is retained or not
     */
    private String pensionRetainedReason;

    // ================================================================
    // RULE CONFIGURATION
    // ================================================================
    
    /**
     * Cutoff date used for Security Forces rule
     * Fixed at July 1, 2024
     */
    private LocalDate cutoffDate;
}
