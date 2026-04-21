package com.claim.claim_processing.common.entities.claim;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CLAIM_ELIGIBILITY_CATEGORY_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CLAIM_ELIGIBILITY_RULE_CAT",
                        columnNames = {"RULE_ID", "CATEGORY_ID"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEligibilityCategoryMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // 🔥 Relation to Rule
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "RULE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_RULE")
    )
    private ClaimEligibilityMaster rule;

    // 🔥 Relation to Category
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_CATEGORY")
    )
    private AgencyCategory category;
}