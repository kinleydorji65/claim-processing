package com.claim.claim_processing.application.entity.detail;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.WrongRemittanceForfeited;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.WrongRemittanceRecalculatedMonth;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "WRONG_REMITANCE",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemitance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ===== FOREIGN KEY TO CLAIM APPLICATION (MANY-TO-ONE) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID")
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false, unique = true)
    private ClaimDetail claimDetail;

    // ===== MEMBER INFORMATION =====
    @Column(name = "NPPF_NUMBER", nullable = false, length = 50)
    private String nppfNumber;

    @Column(name = "MEMBER_NAME", length = 200)
    private String memberName;

    @Column(name = "TARGET_YEAR", nullable = false, length = 20)
    private String targetYear;

    // ===== OPENING BALANCES =====
    // PF (PROVIDENT FUND)
    @Column(name = "OPENING_PF_MC", precision = 20, scale = 2)
    private BigDecimal openingPfMc;

    @Column(name = "OPENING_PF_EC", precision = 20, scale = 2)
    private BigDecimal openingPfEc;

    @Column(name = "OPENING_PF_IMC", precision = 20, scale = 2)
    private BigDecimal openingPfImc;

    @Column(name = "OPENING_PF_IEC", precision = 20, scale = 2)
    private BigDecimal openingPfIec;

    // P (PENSION)
    @Column(name = "OPENING_P_MC", precision = 20, scale = 2)
    private BigDecimal openingPMc;

    @Column(name = "OPENING_P_EC", precision = 20, scale = 2)
    private BigDecimal openingPEc;

    @Column(name = "OPENING_P_IMC", precision = 20, scale = 2)
    private BigDecimal openingPImc;

    @Column(name = "OPENING_P_IEC", precision = 20, scale = 2)
    private BigDecimal openingPIec;

    // G (GRATUITY)
    @Column(name = "OPENING_GC", precision = 20, scale = 2)
    private BigDecimal openingGc;

    @Column(name = "OPENING_GIC", precision = 20, scale = 2)
    private BigDecimal openingGic;

    // V (VOLUNTARY)
    @Column(name = "OPENING_VC", precision = 20, scale = 2)
    private BigDecimal openingVc;

    @Column(name = "OPENING_VIC", precision = 20, scale = 2)
    private BigDecimal openingVic;

    // I (INTEREST ON VOLUNTARY & GRATUITY)
    @Column(name = "OPENING_IVC", precision = 20, scale = 2)
    private BigDecimal openingIvc;

    @Column(name = "OPENING_IGC", precision = 20, scale = 2)
    private BigDecimal openingIgc;

    // ===== CLOSING BALANCES =====
    // PF (PROVIDENT FUND)
    @Column(name = "CLOSING_PF_MC", precision = 20, scale = 2)
    private BigDecimal closingPfMc;

    @Column(name = "CLOSING_PF_EC", precision = 20, scale = 2)
    private BigDecimal closingPfEc;

    @Column(name = "CLOSING_PF_IMC", precision = 20, scale = 2)
    private BigDecimal closingPfImc;

    @Column(name = "CLOSING_PF_IEC", precision = 20, scale = 2)
    private BigDecimal closingPfIec;

    // P (PENSION)
    @Column(name = "CLOSING_P_MC", precision = 20, scale = 2)
    private BigDecimal closingPMc;

    @Column(name = "CLOSING_P_EC", precision = 20, scale = 2)
    private BigDecimal closingPEc;

    @Column(name = "CLOSING_P_IMC", precision = 20, scale = 2)
    private BigDecimal closingPImc;

    @Column(name = "CLOSING_P_IEC", precision = 20, scale = 2)
    private BigDecimal closingPIec;

    // G (GRATUITY)
    @Column(name = "CLOSING_GC", precision = 20, scale = 2)
    private BigDecimal closingGc;

    @Column(name = "CLOSING_GIC", precision = 20, scale = 2)
    private BigDecimal closingGic;

    // V (VOLUNTARY)
    @Column(name = "CLOSING_VC", precision = 20, scale = 2)
    private BigDecimal closingVc;

    @Column(name = "CLOSING_VIC", precision = 20, scale = 2)
    private BigDecimal closingVic;

    // I (INTEREST ON VOLUNTARY & GRATUITY)
    @Column(name = "CLOSING_IVC", precision = 20, scale = 2)
    private BigDecimal closingIvc;

    @Column(name = "CLOSING_IGC", precision = 20, scale = 2)
    private BigDecimal closingIgc;

    // ===== RECALCULATION TOTALS =====
    @Column(name = "TOTAL_RECALCULATED_CONTRIBUTIONS", precision = 20, scale = 2)
    private BigDecimal totalRecalculatedContributions;

    @Column(name = "TOTAL_RECALCULATED_INTEREST", precision = 20, scale = 2)
    private BigDecimal totalRecalculatedInterest;

    @Column(name = "TOTAL_RECALCULATED_AMOUNT", precision = 20, scale = 2)
    private BigDecimal totalRecalculatedAmount;

    // ===== CONFIGURATION =====
    @Column(name = "WITH_INTEREST", length = 1)
    @Builder.Default
    private String withInterest = "N";

    @Column(name = "APPLIED_INTEREST_RATE", precision = 10, scale = 4)
    private BigDecimal appliedInterestRate;

    @Column(name = "YEAR_BASIS")
    private Integer yearBasis;

    @Column(name = "CALCULATION_DATE")
    private LocalDate calculationDate;

    // ===== SELECTED MONTHS =====
    @Column(name = "SELECTED_MONTHS", columnDefinition = "CLOB")
    private String selectedMonths;

    @Column(name = "SELECTED_MONTH_COUNT")
    private Integer selectedMonthCount;

    // ===== STATUS AND METADATA =====
    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "MESSAGE", columnDefinition = "CLOB")
    private String message;

    @Column(name = "YEARS_PROCESSED")
    private Integer yearsProcessed;

    @Column(name = "FROM_YEAR", length = 20)
    private String fromYear;

    @Column(name = "TO_YEAR", length = 20)
    private String toYear;

    // ===== AUDIT FIELDS =====
    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    // ===== ONE-TO-MANY RELATIONSHIP WITH CALCULATION COMPONENTS =====
    @OneToMany(mappedBy = "wrongRemitance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WrongRemittanceCalculationComponent> calculationComponents = new ArrayList<>();

    // ===== ONE-TO-MANY RELATIONSHIP WITH FORFEITED COMPONENTS =====
    @OneToMany(mappedBy = "wrongRemitance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WrongRemittanceForfeited> forfeitedComponents = new ArrayList<>();

    @OneToMany(mappedBy = "wrongRemitance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WrongRemittanceRecalculatedMonth> recalculatedMonths = new ArrayList<>();

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
        if (withInterest == null) {
            withInterest = "N";
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
