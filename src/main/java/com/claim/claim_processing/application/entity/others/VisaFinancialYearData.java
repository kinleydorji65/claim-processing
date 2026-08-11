package com.claim.claim_processing.application.entity.others;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VISA_FINANCIAL_YEAR_DATA", 
       schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaFinancialYearData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ONE-TO-ONE with VisaResponseData (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VISA_RESPONSE_DATA_ID", referencedColumnName = "ID")
    private VisaResponseData visaResponseData;

    @Column(name = "FINANCIAL_YEAR", length = 10, nullable = false)
    private String financialYear;

    // ============ OPENING BALANCES ============
    @Column(name = "OPENING_PF_MC", precision = 19, scale = 2)
    private BigDecimal openingPfMc;

    @Column(name = "OPENING_PF_EC", precision = 19, scale = 2)
    private BigDecimal openingPfEc;

    @Column(name = "OPENING_PF_IMC", precision = 19, scale = 2)
    private BigDecimal openingPfImc;

    @Column(name = "OPENING_PF_IEC", precision = 19, scale = 2)
    private BigDecimal openingPfIec;

    @Column(name = "OPENING_PF_TOTAL", precision = 19, scale = 2)
    private BigDecimal openingPfTotal;

    @Column(name = "OPENING_PC_MC", precision = 19, scale = 2)
    private BigDecimal openingPcMc;

    @Column(name = "OPENING_PC_EC", precision = 19, scale = 2)
    private BigDecimal openingPcEc;

    @Column(name = "OPENING_PC_IMC", precision = 19, scale = 2)
    private BigDecimal openingPcImc;

    @Column(name = "OPENING_PC_IEC", precision = 19, scale = 2)
    private BigDecimal openingPcIec;

    @Column(name = "OPENING_PC_TOTAL", precision = 19, scale = 2)
    private BigDecimal openingPcTotal;

    @Column(name = "OPENING_GRAND_TOTAL", precision = 19, scale = 2)
    private BigDecimal openingGrandTotal;

    // ============ TRANSACTION DURING YEAR ============
    @Column(name = "TRANSACTION_PF_MC", precision = 19, scale = 2)
    private BigDecimal transactionPfMc;

    @Column(name = "TRANSACTION_PF_EC", precision = 19, scale = 2)
    private BigDecimal transactionPfEc;

    @Column(name = "TRANSACTION_PF_IMC", precision = 19, scale = 2)
    private BigDecimal transactionPfImc;

    @Column(name = "TRANSACTION_PF_IEC", precision = 19, scale = 2)
    private BigDecimal transactionPfIec;

    @Column(name = "TRANSACTION_PF_TOTAL", precision = 19, scale = 2)
    private BigDecimal transactionPfTotal;

    @Column(name = "TRANSACTION_PC_MC", precision = 19, scale = 2)
    private BigDecimal transactionPcMc;

    @Column(name = "TRANSACTION_PC_EC", precision = 19, scale = 2)
    private BigDecimal transactionPcEc;

    @Column(name = "TRANSACTION_PC_IMC", precision = 19, scale = 2)
    private BigDecimal transactionPcImc;

    @Column(name = "TRANSACTION_PC_IEC", precision = 19, scale = 2)
    private BigDecimal transactionPcIec;

    @Column(name = "TRANSACTION_PC_TOTAL", precision = 19, scale = 2)
    private BigDecimal transactionPcTotal;

    @Column(name = "TRANSACTION_GRAND_TOTAL", precision = 19, scale = 2)
    private BigDecimal transactionGrandTotal;

    // ============ EXCESS TRANSFERRED ============
    @Column(name = "EXCESS_PC_MC", precision = 19, scale = 2)
    private BigDecimal excessPcMc;

    @Column(name = "EXCESS_PC_EC", precision = 19, scale = 2)
    private BigDecimal excessPcEc;

    @Column(name = "EXCESS_PC_IMC", precision = 19, scale = 2)
    private BigDecimal excessPcImc;

    @Column(name = "EXCESS_PC_IEC", precision = 19, scale = 2)
    private BigDecimal excessPcIec;

    @Column(name = "EXCESS_TOTAL", precision = 19, scale = 2)
    private BigDecimal excessTotal;

    // ============ CLOSING BALANCES ============
    @Column(name = "CLOSING_PF_MC", precision = 19, scale = 2)
    private BigDecimal closingPfMc;

    @Column(name = "CLOSING_PF_EC", precision = 19, scale = 2)
    private BigDecimal closingPfEc;

    @Column(name = "CLOSING_PF_IMC", precision = 19, scale = 2)
    private BigDecimal closingPfImc;

    @Column(name = "CLOSING_PF_IEC", precision = 19, scale = 2)
    private BigDecimal closingPfIec;

    @Column(name = "CLOSING_PF_TOTAL", precision = 19, scale = 2)
    private BigDecimal closingPfTotal;

    @Column(name = "CLOSING_PC_MC", precision = 19, scale = 2)
    private BigDecimal closingPcMc;

    @Column(name = "CLOSING_PC_EC", precision = 19, scale = 2)
    private BigDecimal closingPcEc;

    @Column(name = "CLOSING_PC_IMC", precision = 19, scale = 2)
    private BigDecimal closingPcImc;

    @Column(name = "CLOSING_PC_IEC", precision = 19, scale = 2)
    private BigDecimal closingPcIec;

    @Column(name = "CLOSING_PC_TOTAL", precision = 19, scale = 2)
    private BigDecimal closingPcTotal;

    @Column(name = "CLOSING_GRAND_TOTAL", precision = 19, scale = 2)
    private BigDecimal closingGrandTotal;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}