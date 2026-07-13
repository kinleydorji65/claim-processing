package com.claim.claim_processing.integration.contribution.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * T-84: Read-only view of ARR_CONFIGURATION in master schema.
 * ARR = Actual Rate of Return — declared annually by NPPF board.
 * Write operations go through master-service CRUD endpoints.
 *
 * ARR_STATUS values:
 *   PROVISIONAL  — estimated rate used for mid-year interest calc
 *   DECLARED     — board-approved final rate for the year
 *   APPLIED      — year-end interest crediting completed
 *
 * Year start/end dates are copied from ACCOUNTING_YEAR_MASTER at creation time
 * to avoid cross-schema joins at interest calculation time.
 */
@Entity
@Table(name = "ARR_CONFIGURATION", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** Accounting year key, e.g. "2024-2025". Unique per year. */
    @Column(name = "ACCOUNTING_YEAR", nullable = false, unique = true, length = 20)
    private String accountingYear;

    /** Annual rate of return, e.g. 8.5000 (percent). */
    @Column(name = "ARR_RATE", nullable = false, precision = 10, scale = 4)
    private BigDecimal arrRate;

    /** PROVISIONAL / DECLARED / APPLIED */
    @Column(name = "ARR_STATUS", nullable = false, length = 20)
    @Builder.Default
    private String arrStatus = "PROVISIONAL";

    /** Year start date — sourced from ACCOUNTING_YEAR_MASTER. */
    @Column(name = "YEAR_START_DATE", nullable = false)
    private LocalDate yearStartDate;

    /** Year end date — sourced from ACCOUNTING_YEAR_MASTER. */
    @Column(name = "YEAR_END_DATE", nullable = false)
    private LocalDate yearEndDate;

    /** 'Y' if the year_basis denominator should be 366. */
    @Column(name = "IS_LEAP_YEAR", length = 1)
    @Builder.Default
    private String isLeapYear = "N";

    /** Date the board officially declared the ARR. Null until DECLARED. */
    @Column(name = "DECLARED_DATE")
    private LocalDate declaredDate;

    /** Board resolution reference or document number. */
    @Column(name = "DECLARATION_REF", length = 100)
    private String declarationRef;

    /** Total surplus for the year (for FAD/board back-calculation reference). */
    @Column(name = "SURPLUS_AMOUNT", precision = 18, scale = 2)
    private BigDecimal surplusAmount;

    /** Total principal (fund corpus) for the year. */
    @Column(name = "PRINCIPAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal principalAmount;

    /** Interest amount to refund (deducted from surplus before rate calculation). */
    @Column(name = "INTEREST_TO_REFUND", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal interestToRefund = BigDecimal.ZERO;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    // ── Derived helpers ───────────────────────────────────────────────────────

    /** Year basis for pro-rata calculation: 366 if leap year, else 365. */
    @Transient
    public int getYearBasis() {
        return "Y".equalsIgnoreCase(isLeapYear) ? 366 : 365;
    }

    /** True when ARR is officially declared (not provisional). */
    @Transient
    public boolean isDeclared() {
        return "DECLARED".equals(arrStatus) || "APPLIED".equals(arrStatus);
    }
}

