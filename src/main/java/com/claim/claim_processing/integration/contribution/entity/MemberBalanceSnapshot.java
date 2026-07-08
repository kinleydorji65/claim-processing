package com.claim.claim_processing.integration.contribution.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One row per member per accounting year.
 * Updated on: confirmPosting, interest post, reversal (full recalculate).
 * Unique key: (CID, NPPF_NUMBER, ACCOUNTING_YEAR).
 */
@Entity
@Table(
        name = "MEMBER_BALANCE_SNAPSHOT",
        schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_MBS_MEMBER_YEAR",
                columnNames = {"CID", "NPPF_NUMBER", "ACCOUNTING_YEAR"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBalanceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CID", nullable = false, length = 50)
    private String cid;

    @Column(name = "NPPF_NUMBER", nullable = false, length = 50)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", length = 50)
    private String agencyCode;

    @Column(name = "ACCOUNTING_YEAR", nullable = false, length = 20)
    private String accountingYear;

    // ── Contribution components ───────────────────────────────────────────────

    @Builder.Default
    @Column(name = "PF_EC", precision = 20, scale = 4, nullable = false)
    private BigDecimal pfEc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "PF_MC", precision = 20, scale = 4, nullable = false)
    private BigDecimal pfMc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "PENSION_EC", precision = 20, scale = 4, nullable = false)
    private BigDecimal pensionEc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "GC", precision = 20, scale = 4, nullable = false)
    private BigDecimal gc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "VC", precision = 20, scale = 4, nullable = false)
    private BigDecimal vc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "TOTAL_CONTRIBUTIONS", precision = 20, scale = 4, nullable = false)
    private BigDecimal totalContributions = BigDecimal.ZERO;

    // ── Interest components ───────────────────────────────────────────────────

    @Builder.Default
    @Column(name = "INTEREST_EC", precision = 20, scale = 4, nullable = false)
    private BigDecimal interestEc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "INTEREST_MC", precision = 20, scale = 4, nullable = false)
    private BigDecimal interestMc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "INTEREST_PENSION", precision = 20, scale = 4, nullable = false)
    private BigDecimal interestPension = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "INTEREST_GC", precision = 20, scale = 4, nullable = false)
    private BigDecimal interestGc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "INTEREST_VC", precision = 20, scale = 4, nullable = false)
    private BigDecimal interestVc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "TOTAL_INTEREST", precision = 20, scale = 4, nullable = false)
    private BigDecimal totalInterest = BigDecimal.ZERO;

    // ── Totals ────────────────────────────────────────────────────────────────

    @Builder.Default
    @Column(name = "TOTAL_BALANCE", precision = 20, scale = 4, nullable = false)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    // ── Audit ─────────────────────────────────────────────────────────────────

    @Column(name = "LAST_UPDATED_AT", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 100)
    private String lastUpdatedBy;
}

