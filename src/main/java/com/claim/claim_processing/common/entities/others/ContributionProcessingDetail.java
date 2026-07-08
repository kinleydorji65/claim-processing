package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRIBUTION_PROCESSING_DETAIL", schema = "PPFMS_CONTRIBUTION_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionProcessingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, precision = 19)
    private Long id;

    @Column(name = "PID", nullable = false, precision = 19)
    private Long pid;

    @Column(name = "BATCH_ID", nullable = false, precision = 19)
    private Long batchId;

    @Column(name = "CID", nullable = false, length = 255)
    private String cid;

    @Column(name = "NPPF_NUMBER", nullable = false, length = 255)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", nullable = false, length = 255)
    private String agencyCode;

    @Column(name = "BASIC_SALARY", precision = 20, scale = 4)
    private BigDecimal basicSalary;

    @Column(name = "EC", precision = 20, scale = 4)
    private BigDecimal ec;

    @Column(name = "MC", precision = 20, scale = 4)
    private BigDecimal mc;

    @Column(name = "GC", precision = 20, scale = 4)
    private BigDecimal gc;

    @Column(name = "VC", precision = 20, scale = 4)
    private BigDecimal vc;

    @Column(name = "MONTH_NAME", nullable = false, length = 50)
    private String monthName;

    @Column(name = "YEAR", nullable = false, length = 10)
    private String year;

    @Column(name = "VALIDATION_STATUS", length = 100)
    private String validationStatus;

    @Column(name = "POSTING_STATUS", length = 100)
    private String postingStatus;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 255)
    private String updatedBy;

}
