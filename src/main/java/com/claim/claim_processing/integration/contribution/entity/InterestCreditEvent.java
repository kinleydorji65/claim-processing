package com.claim.claim_processing.integration.contribution.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * T-91: Year-end interest credit event header.
 * Maps to INTEREST_CREDIT_EVENT (PPFMS_CONTRIBUTION_SERVICE_SCHEMA).
 *
 * One row per accounting year per run (ACCOUNTING_YEAR is unique).
 * STATUS lifecycle: INITIATED → CALCULATED → APPROVED → POSTED [→ ADJUSTED]
 *
 * EVENT_REF pattern: ICE{year4digit}{6-digit seq from INTEREST_CREDIT_EVENT_SEQ}
 * e.g. ICE2025000001
 *
 * IS_PROVISIONAL = 'Y' when run before the year's ARR is officially DECLARED.
 * If provisional: when ARR is later declared, a second ADJUSTED run posts the delta.
 */
@Entity
@Table(
        name = "INTEREST_CREDIT_EVENT",
        schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestCreditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** ICE{year}{6-digit seq}, e.g. ICE2025000001. Unique. */
    @Column(name = "EVENT_REF", nullable = false, unique = true, length = 50)
    private String eventRef;

    /** Accounting year this event credits interest for, e.g. "2024-2025". Unique. */
    @Column(name = "ACCOUNTING_YEAR", nullable = false, unique = true, length = 20)
    private String accountingYear;

    /** ARR rate used for this run */
    @Column(name = "ARR_RATE", nullable = false, precision = 10, scale = 4)
    private BigDecimal arrRate;

    /** 'Y' if this run used a provisional (not yet declared) rate */
    @Column(name = "IS_PROVISIONAL", length = 1)
    @Builder.Default
    private String isProvisional = "N";

    /** Total member-component records calculated */
    @Column(name = "TOTAL_MEMBERS")
    @Builder.Default
    private Integer totalMembers = 0;

    /** SUM of all TOTAL_INTEREST across all MEMBER_INTEREST_RECORD rows */
    @Column(name = "TOTAL_INTEREST", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal totalInterest = BigDecimal.ZERO;

    /** INITIATED / CALCULATED / APPROVED / POSTED / ADJUSTED */
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private String status = "INITIATED";

    @Column(name = "INITIATED_BY", nullable = false, length = 100)
    private String initiatedBy;

    @Column(name = "INITIATED_AT")
    private LocalDateTime initiatedAt;

    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

    @Column(name = "POSTED_BY", length = 100)
    private String postedBy;

    @Column(name = "POSTED_AT")
    private LocalDateTime postedAt;

    /** FK to the first ACCOUNTING_EVENT created at post() time */
    @Column(name = "ACCOUNTING_EVENT_ID")
    private Long accountingEventId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}

