package com.claim.claim_processing.common.entities.claim;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CLAIM_TYPE_RULE_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeRuleMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CLAIM_TYPE_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private ClaimTypeMaster claimType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "RULE_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private RuleTypeMaster ruleType;

}