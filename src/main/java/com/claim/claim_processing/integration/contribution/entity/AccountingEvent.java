package com.claim.claim_processing.integration.contribution.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * GL transaction header. One balanced double-entry document per event.
 * EVENT_REF pattern: NGN{calYear}{6-digit seq} (D-036) — NGN prefix prevents
 * collision with legacy Premia doc-number ranges in FAD staging.
 * Idempotency: one active (status != REVERSED) event per (BATCH_ID, EVENT_TYPE) —
 * enforced in LedgerPostingService (guard G4).
 */
@Entity
@Table(name = "ACCOUNTING_EVENT", schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EVENT_REF", nullable = false, unique = true, length = 100)
    private String eventRef;

    // CONTRIBUTION_POSTING / REVERSAL / REFUND / PENALTY_PAYMENT / YEAR_END_CLOSE
    @Column(name = "EVENT_TYPE", nullable = false, length = 50)
    private String eventType;

    // Nullable — YEAR_END_CLOSE has no batch
    @Column(name = "BATCH_ID")
    private Long batchId;

    // AGENCY_CATEGORIES.CATEGORY_ID ('01' Civil / '03' AF / '04' Private, D-042)
    @Column(name = "AGENCY_CATEGORY_ID", length = 50)
    private String agencyCategoryId;

    @Column(name = "AGENCY_CODE", length = 50)
    private String agencyCode;

    @Column(name = "MONTH_NAME", length = 20)
    private String monthName;

    @Column(name = "YEAR", length = 20)
    private String year;

    @Column(name = "ACCOUNTING_YEAR", length = 20)
    private String accountingYear;

    @Column(name = "TRAN_CODE", length = 20)
    private String tranCode;

    // POSTED / REVERSED
    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "TOTAL_DR", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDr;

    @Column(name = "TOTAL_CR", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCr;

    // Self-FK by id — no JPA relationship (standards)
    @Column(name = "REVERSAL_OF_EVENT_ID")
    private Long reversalOfEventId;

    @Column(name = "NARRATION", length = 1000)
    private String narration;

    @Column(name = "POSTED_BY", length = 100)
    private String postedBy;

    @Column(name = "POSTED_AT")
    private LocalDateTime postedAt;
}

