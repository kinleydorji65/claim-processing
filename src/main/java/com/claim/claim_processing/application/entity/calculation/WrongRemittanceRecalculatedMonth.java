package com.claim.claim_processing.application.entity.calculation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.application.entity.detail.WrongRemitance;

@Entity
@Table(
        name = "WRONG_REMITTANCE_RECALCULATED_MONTH",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemittanceRecalculatedMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ===== FOREIGN KEY TO WRONG_REMITANCE =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRONG_REMITANCE_ID", nullable = false)
    private WrongRemitance wrongRemitance;

    // ===== MONTH INFORMATION =====
    @Column(name = "MONTH", length = 20, nullable = false)
    private String month;

    @Column(name = "MONTH_NAME", length = 20)
    private String monthName;

    @Column(name = "INVOICE_DATE")
    private LocalDate invoiceDate;

    @Column(name = "DAYS_FOR_INTEREST", precision = 20, scale = 2)
    private BigDecimal daysForInterest;

    @Column(name = "INTEREST_RATE", precision = 20, scale = 4)
    private BigDecimal interestRate;

    // ===== PF COMPONENTS =====
    @Column(name = "PF_MC", precision = 20, scale = 2)
    private BigDecimal pfMc;

    @Column(name = "PF_EC", precision = 20, scale = 2)
    private BigDecimal pfEc;

    @Column(name = "PF_IMC", precision = 20, scale = 2)
    private BigDecimal pfImc;

    @Column(name = "PF_IEC", precision = 20, scale = 2)
    private BigDecimal pfIec;

    // ===== PENSION COMPONENTS =====
    @Column(name = "P_MC", precision = 20, scale = 2)
    private BigDecimal pMc;

    @Column(name = "P_EC", precision = 20, scale = 2)
    private BigDecimal pEc;

    @Column(name = "P_IMC", precision = 20, scale = 2)
    private BigDecimal pImc;

    @Column(name = "P_IEC", precision = 20, scale = 2)
    private BigDecimal pIec;

    // ===== GRATUITY COMPONENTS =====
    @Column(name = "GC", precision = 20, scale = 2)
    private BigDecimal gc;

    @Column(name = "GIC", precision = 20, scale = 2)
    private BigDecimal gic;

    // ===== VOLUNTARY COMPONENTS =====
    @Column(name = "VC", precision = 20, scale = 2)
    private BigDecimal vc;

    @Column(name = "VIC", precision = 20, scale = 2)
    private BigDecimal vic;

    // ===== INTEREST ON VOLUNTARY & GRATUITY =====
    @Column(name = "IVC", precision = 20, scale = 2)
    private BigDecimal ivc;

    @Column(name = "IGC", precision = 20, scale = 2)
    private BigDecimal igc;

    // ===== TOTALS =====
    @Column(name = "TOTAL_CONTRIBUTION", precision = 20, scale = 2)
    private BigDecimal totalContribution;

    @Column(name = "TOTAL_INTEREST", precision = 20, scale = 2)
    private BigDecimal totalInterest;

    @Column(name = "TOTAL_AMOUNT", precision = 20, scale = 2)
    private BigDecimal totalAmount;

    // ===== STATUS =====
    @Column(name = "STATUS", length = 50)
    private String status;

    // ===== AUDIT FIELDS =====
    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
        if (updatedBy == null) {
            updatedBy = "SYSTEM";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
        if (updatedBy == null) {
            updatedBy = "SYSTEM";
        }
    }
}
