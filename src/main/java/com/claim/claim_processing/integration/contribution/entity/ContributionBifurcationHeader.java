package com.claim.claim_processing.integration.contribution.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRIBUTION_BIFURCATION_HEADER",
        schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionBifurcationHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIF_ID")
    private Long bifId;

    @Column(name = "PID", nullable = false)
    private Long pid;

    @Column(name = "BATCH_ID", nullable = false)
    private Long batchId;

    @Column(name = "AGENCY_CODE", nullable = false, length = 50)
    private String agencyCode;

    @Column(name = "MONTH_NAME", nullable = false, length = 20)
    private String monthName;

    @Column(name = "YEAR", nullable = false, length = 20)
    private String year;

    @Column(name = "TOTAL_MEMBERS")
    private Integer totalMembers;

    @Column(name = "TOTAL_PENSION_MC", precision = 20, scale = 4)
    private BigDecimal totalPensionMc;

    @Column(name = "TOTAL_PENSION_EC", precision = 20, scale = 4)
    private BigDecimal totalPensionEc;

    @Column(name = "TOTAL_PF_MC", precision = 20, scale = 4)
    private BigDecimal totalPfMc;

    @Column(name = "TOTAL_PF_EC", precision = 20, scale = 4)
    private BigDecimal totalPfEc;

    // BIFURCATED or FAILED
    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}

