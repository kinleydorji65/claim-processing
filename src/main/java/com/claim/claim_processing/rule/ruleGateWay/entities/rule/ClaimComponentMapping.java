package com.claim.claim_processing.rule.ruleGateWay.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CLAIM_COMPONENT_MAPPING", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CLAIM_COMPONENT_MAPPING_CODE", columnNames = "COMPONENT_MAPPING_CODE")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimComponentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COMPONENT_MAPPING_CODE", nullable = false, length = 100)
    private String componentMappingCode;

    @Column(name = "HAS_PF", nullable = false, length = 1)
    private String hasPf;

    @Column(name = "HAS_PC", nullable = false, length = 1)
    private String hasPc;

    @Column(name = "HAS_EC", nullable = false, length = 1)
    private String hasEc;

    @Column(name = "HAS_MC", nullable = false, length = 1)
    private String hasMc;

    @Column(name = "HAS_IMC", nullable = false, length = 1)
    private String hasImc;

    @Column(name = "HAS_IEC", nullable = false, length = 1)
    private String hasIec;

    @Column(name = "HAS_GC", nullable = false, length = 1)
    private String hasGc;

    @Column(name = "HAS_GIC", nullable = false, length = 1)
    private String hasGic;

    @Column(name = "HAS_VC", nullable = false, length = 1)
    private String hasVc;

    @Column(name = "HAS_VIC", nullable = false, length = 1)
    private String hasVic;

    @Column(name = "EFFECTIVE_FROM", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @OneToMany(mappedBy = "componentMapping", fetch = FetchType.LAZY)
private List<ClaimComponentExpressionMapping> expressions;

}
