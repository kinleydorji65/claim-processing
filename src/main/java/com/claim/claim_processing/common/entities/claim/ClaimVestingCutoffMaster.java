package com.claim.claim_processing.common.entities.claim;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLAIM_VESTING_CUTOFF_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimVestingCutoffMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "RULE_CODE")
    private String ruleCode;
    
    @Column(name = "CUTOFF_DATE")
    private LocalDate cutoffDate;
    
    @Column(name = "REQUIRED_MONTHS")
    private Integer requiredMonths;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "IS_ACTIVE")
    private String isActive;
}


