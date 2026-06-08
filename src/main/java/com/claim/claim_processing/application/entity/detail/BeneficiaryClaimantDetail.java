package com.claim.claim_processing.application.entity.detail;


import com.claim.claim_processing.common.entities.others.RelationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.member.MemberFamily;
import com.claim.claim_processing.common.entities.others.member.MemberNominee;

@Entity
@Table(
        name = "BENEFICIARY_CLAIMANT_DETAIL",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryClaimantDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "NOMINEE_ID",
            foreignKey = @ForeignKey(name = "FK_BCD_NOMINEE")
    )
    private MemberNominee nominee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DEPENDENT_ID",
            foreignKey = @ForeignKey(name = "FK_BCD_DEPENDENT")
    )
    private MemberFamily dependent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIMANT_TYPE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_BCD_CLAIMANT_TYPE")
    )
    private ClaimantTypeMaster claimantType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "BENEFICIARY_SETTLEMENT_DETAIL_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_BCD_SETTLEMENT")
    )
    private BeneficiarySettlementDetail beneficiarySettlementDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "RELATIONSHIP_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_BCD_RELATIONSHIP_TYPE")
    )
    private RelationType relationshipType;

    @Column(name = "BENEFICIARY_IDENTIFIER", length = 100)
    private String beneficiaryIdentifier;

    @Column(name = "BENEFICIARY_NAME", length = 200)
    private String beneficiaryName;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "BENEFICIARY_SHARE_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal beneficiarySharePercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYEE_TYPE_ID", foreignKey = @ForeignKey(name = "FK_BCD_PAYEE_TYPE"))
    private PayeeTypeMaster payeeType;

    @Column(name = "IS_MEMBER_FAMILY", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum isMemberFamily = ActivityEnum.N;

    @Column(name = "IS_MINOR", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum isMinor = ActivityEnum.N;

    @Column(name = "GUARDIAN_NAME", length = 200)
    private String guardianName;

    @Column(name = "GUARDIAN_IDENTIFIER", length = 100)
    private String guardianIdentifier;

    @Column(name = "BENEFIT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal benefitAmount;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (isMemberFamily == null) {
            isMemberFamily = ActivityEnum.N;
        }
        if (isMinor == null) {
            isMinor = ActivityEnum.N;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
