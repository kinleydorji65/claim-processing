package com.claim.claim_processing.application.entity.others;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VISA_RESPONSE_DATA", 
       schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaResponseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ONE-TO-ONE with VisaDownloaded (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VISA_DOWNLOADED_ID", referencedColumnName = "ID")
    private VisaDownloaded visaDownloaded;

    @Column(name = "RESPONSE_DATA", columnDefinition = "CLOB")
    @JdbcTypeCode(SqlTypes.CLOB)
    private String responseData;

    @Column(name = "JOINING_DATE")
    private LocalDateTime joiningDate;

    @Column(name = "CALCULATION_DATE")
    private LocalDateTime calculationDate;

    @Column(name = "FIRST_CONTRIBUTION_DATE")
    private LocalDateTime firstContributionDate;

    @Column(name = "LAST_CONTRIBUTION_DATE")
    private LocalDateTime lastContributionDate;

    @Column(name = "TOTAL_PRINCIPAL", precision = 19, scale = 2)
    private BigDecimal totalPrincipal;

    @Column(name = "TOTAL_INTEREST", precision = 19, scale = 2)
    private BigDecimal totalInterest;

    @Column(name = "TOTAL_BALANCE", precision = 19, scale = 2)
    private BigDecimal totalBalance;

    @Column(name = "CURRENT_YEAR", length = 10)
    private String currentYear;

    @Column(name = "CURRENT_YEAR_RATE", precision = 19, scale = 4)
    private BigDecimal currentYearRate;

    @Column(name = "CURRENT_YEAR_BASIS")
    private Integer currentYearBasis;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "RESPONSE_MESSAGE", length = 1000)
    private String responseMessage;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    // ONE-TO-ONE with VisaFinancialYearData (inverse side)
    @OneToOne(mappedBy = "visaResponseData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VisaFinancialYearData financialYearData;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}