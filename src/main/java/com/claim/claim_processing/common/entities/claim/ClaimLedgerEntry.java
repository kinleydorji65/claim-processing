package com.claim.claim_processing.common.entities.claim;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_LEDGER_ENTRY", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNTING_EVENT_ID", nullable = false)
    private Long accountingEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNTING_EVENT_ID", insertable = false, updatable = false)
    private ClaimAccountingEvent accountingEvent;

    @Column(name = "SEQ_NO", nullable = false)
    private Integer seqNo;

    @Column(name = "MAIN_ACCOUNT_CODE", nullable = false)
    private String mainAccountCode;

    @Column(name = "SUB_ACCOUNT_CODE", nullable = false)
    private String subAccountCode;

    @Column(name = "DRCR", nullable = false)
    private String drcr;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "ENTRY_ROLE")
    private String entryRole;

    @Column(name = "COMPONENT_CODE")
    private String componentCode;

    @Column(name = "NARRATION")
    private String narration;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
