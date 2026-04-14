package com.claim.claim_processing.master.entities.claim;

import com.claim.claim_processing.master.entities.others.AgencyCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CLAIM_LAPSED_REFUND_CATEGORY_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_LAPSED_REFUND_RULE_CATEGORY",
                        columnNames = {"RULE_ID", "CATEGORY_ID"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimLapsedRefundCategoryMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "RULE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_LAPSED_REFUND_CATEGORY_RULE")
    )
    private ClaimLapsedRefundMaster rule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_LAPSED_REFUND_CATEGORY")
    )
    private AgencyCategory category;
}