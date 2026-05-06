package com.claim.claim_processing.common.entities.claim;

import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "VESTING_REFUND_BENEFIT_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VestingRefundBenefitMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BENEFITE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_BENEFIT_TYPE_MAP")
    )
    private BenefitComponentTypeMaster benefitComponentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFUND_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_VESTING_REFUND_TYPE_MAP")
    )
    private VestingRefundType vestingRefundType;
}
// 