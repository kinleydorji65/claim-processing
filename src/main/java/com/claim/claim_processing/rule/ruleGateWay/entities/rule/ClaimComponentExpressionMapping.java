package com.claim.claim_processing.rule.ruleGateWay.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "CLAIM_COMPONENT_EXPRESSION_MAPPING",
        schema = "PPFMS_MASTER_SERVICE_SCHEMA"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimComponentExpressionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "COMPONENT_MAPPING_CODE",
            referencedColumnName = "COMPONENT_MAPPING_CODE",
            nullable = false
    )
    private ClaimComponentMapping componentMapping;

    @Column(name = "EXPRESSION", nullable = false, length = 1000)
    private String expression;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
