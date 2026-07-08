package com.claim.claim_processing.integration.contribution.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "MEMBER_INTEREST_RECORD",
        schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_MIR_KEY",
                columnNames = {"CID", "NPPF_NUMBER", "ACCOUNTING_YEAR", "COMPONENT_CODE"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInterestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CID", nullable = false, length = 50)
    private String cid;

    @Column(name = "NPPF_NUMBER", nullable = false, length = 50)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", nullable = false, length = 50)
    private String agencyCode;

    /** '01' = Civil, '03' = Armed Forces, '04' = Private */
    @Column(name = "AGENCY_CATEGORY_ID", nullable = false, length = 10)
    private String agencyCategoryId;

    /** e.g. "2024-2025" */
    @Column(name = "ACCOUNTING_YEAR", nullable = false, length = 20)
    private String accountingYear;

    /** IEC / IMC / IPC / IGC / IVC */
    @Column(name = "COMPONENT_CODE", nullable = false, length = 20)
    private String componentCode;

    /** Balance carried forward from prior year MIR; 0 for new members. */
    @Column(name = "OPENING_BALANCE", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /** SUM of bifurcation detail amounts posted in this accounting year. */
    @Column(name = "CONTRIBUTIONS_YTD", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal contributionsYtd = BigDecimal.ZERO;

    /** Weighted average principal for pro-rata calculation. */
    @Column(name = "PRO_RATA_PRINCIPAL", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal proRataPrincipal = BigDecimal.ZERO;

    /** OPENING_BALANCE + CONTRIBUTIONS_YTD + TOTAL_INTEREST */
    @Column(name = "CLOSING_BALANCE", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal closingBalance = BigDecimal.ZERO;

    /** ARR rate applied, e.g. 8.5000 */
    @Column(name = "ARR_RATE", nullable = false, precision = 10, scale = 4)
    private BigDecimal arrRate;

    /** OPENING_BALANCE × (ARR_RATE / 100) */
    @Column(name = "INTEREST_ON_OPENING", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal interestOnOpening = BigDecimal.ZERO;

    /** SUM of pro-rata interest across all batches in the year */
    @Column(name = "INTEREST_ON_NEW", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal interestOnNew = BigDecimal.ZERO;

    /** INTEREST_ON_OPENING + INTEREST_ON_NEW */
    @Column(name = "TOTAL_INTEREST", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal totalInterest = BigDecimal.ZERO;

    /** CALCULATED / PROVISIONAL / CREDITED / ADJUSTED */
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private String status = "CALCULATED";

    /** 'Y' when run before ARR is officially declared */
    @Column(name = "IS_PROVISIONAL", length = 1)
    @Builder.Default
    private String isProvisional = "N";

    @Column(name = "CALC_DATE")
    private LocalDate calcDate;

    /** FK to ACCOUNTING_EVENT created at post() step */
    @Column(name = "ACCOUNTING_EVENT_ID")
    private Long accountingEventId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}

