package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNTING_INTEREST_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountingInterestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FINANCIAL_YEAR", nullable = false, length = 20)
    private String financialYear;

    @Column(name = "INTEREST_DATE", nullable = false)
    private LocalDate interestDate;

    @Column(name = "INTEREST_RATE", nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive = "Y";

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}
