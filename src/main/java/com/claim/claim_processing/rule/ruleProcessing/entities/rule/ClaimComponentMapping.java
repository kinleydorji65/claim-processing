package com.claim.claim_processing.rule.ruleProcessing.entities.rule;

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

    @Column(name = "HAS_PF_MC", length = 1)
    private String hasPfMc;

    @Column(name = "HAS_PF_EC", length = 1)
    private String hasPfEc;

    @Column(name = "HAS_PF_IMC", length = 1)
    private String hasPfImc;

    @Column(name = "HAS_PF_IEC", length = 1)
    private String hasPfIec;

    @Column(name = "HAS_P_MC", length = 1)
    private String hasPMc;

    @Column(name = "HAS_P_EC", length = 1)
    private String hasPEc;

    @Column(name = "HAS_P_IMC", length = 1)
    private String hasPImc;

    @Column(name = "HAS_P_IEC", length = 1)
    private String hasPIec;

    @Column(name = "HAS_GC", length = 1)
    private String hasGc;

    @Column(name = "HAS_GIC", length = 1)
    private String hasGic;

    @Column(name = "HAS_VC", length = 1)
    private String hasVc;

    @Column(name = "HAS_VIC", length = 1)
    private String hasVic;

    @Column(name = "HAS_IVC", length = 1)
    private String hasIvc;

    @Column(name = "HAS_IGC", length = 1)
    private String hasIgc;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "CREATED_AT")
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
