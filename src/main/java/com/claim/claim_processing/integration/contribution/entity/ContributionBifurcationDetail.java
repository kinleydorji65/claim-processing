package com.claim.claim_processing.integration.contribution.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRIBUTION_BIFURCATION_DETAIL",
        schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionBifurcationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BIF_ID", nullable = false)
    private Long bifId;

    @Column(name = "PID", nullable = false)
    private Long pid;

    @Column(name = "BATCH_ID", nullable = false)
    private Long batchId;

    @Column(name = "CID", nullable = false, length = 50)
    private String cid;

    @Column(name = "NPPF_NUMBER", nullable = false, length = 50)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", nullable = false, length = 50)
    private String agencyCode;

    @Column(name = "AGENCY_CATEGORY", length = 50)
    private String agencyCategory;

    @Column(name = "SCHEME_TYPE", length = 10)
    private String schemeType;

    // Which rule was applied
    @Column(name = "RULE_ID")
    private Long ruleId;

    // MEMBER or CATEGORY — audit trail for which level the rule came from
    @Column(name = "RULE_SOURCE", length = 20)
    private String ruleSource;

    // Original amounts from ContributionProcessingDetail
    @Column(name = "BASIC_SALARY", precision = 20, scale = 4)
    private BigDecimal basicSalary;

    @Column(name = "EC", precision = 20, scale = 4)
    private BigDecimal ec;

    @Column(name = "MC", precision = 20, scale = 4)
    private BigDecimal mc;

    @Column(name = "GC", precision = 20, scale = 4)
    private BigDecimal gc;

    @Column(name = "VC", precision = 20, scale = 4)
    private BigDecimal vc;

    @Column(name = "TOTAL_CONTRIBUTION", precision = 20, scale = 4)
    private BigDecimal totalContribution;

    // Bifurcated amounts
    @Column(name = "PENSION_MC", precision = 20, scale = 4)
    private BigDecimal pensionMc;

    @Column(name = "PENSION_EC", precision = 20, scale = 4)
    private BigDecimal pensionEc;

    @Column(name = "PF_MC", precision = 20, scale = 4)
    private BigDecimal pfMc;

    @Column(name = "PF_EC", precision = 20, scale = 4)
    private BigDecimal pfEc;

    // BIFURCATED / NO_RULE / ERROR
    @Column(name = "POSTING_STATUS", nullable = false, length = 20)
    private String postingStatus;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}

