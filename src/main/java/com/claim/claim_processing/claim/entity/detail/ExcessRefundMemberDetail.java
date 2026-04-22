package com.claim.claim_processing.claim.entity.detail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.contribution.SchemeMaster;

@Entity
@Table(
        name = "EXCESS_REFUND_MEMBER_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcessRefundMemberDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCESS_REFUND_DETAIL_ID", nullable = false)
    private ExcessRefundDetail excessRefundDetail;

    @Column(name = "MEMBER_CODE", length = 100)
    private String memberCode;

    @Column(name = "MEMBER_NPPF_NUMBER", length = 100)
    private String memberNppfNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEME_TYPE_ID")
    private SchemeMaster schemeType;

    @Column(name = "EMPLOYMENT_TYPE", length = 100)
    private String employmentType;

    @Column(name = "DATE_OF_JOINING")
    private LocalDate dateOfJoining;

    @Column(name = "CURRENT_STATUS")
    private Long currentStatus;

    @Column(name = "SCHEDULED_CONTRIBUTION", precision = 15, scale = 2)
    private BigDecimal scheduledContribution;

    @Column(name = "ACTUAL_CONTRIBUTION", precision = 15, scale = 2)
    private BigDecimal actualContribution;

    @Column(name = "CALCULATED_EXCESS", precision = 15, scale = 2)
    private BigDecimal calculatedExcess;

    @Column(name = "REQUESTED_REFUND_AMOUNT", precision = 15, scale = 2)
    private BigDecimal requestedRefundAmount;

    @Column(name = "TOTAL_PAID", precision = 15, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "TOTAL_CONTRIBUTION", precision = 15, scale = 2)
    private BigDecimal totalContribution;

    @Column(name = "TOTAL_EXCESS", precision = 15, scale = 2)
    private BigDecimal totalExcess;

    @Column(name = "MEMBER_REQUESTED_REFUND", precision = 15, scale = 2)
    private BigDecimal memberRequestedRefund;

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
        
        createdAt = new Timestamp(System.currentTimeMillis());
        
        
        updatedAt = new Timestamp(System.currentTimeMillis());
        
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
