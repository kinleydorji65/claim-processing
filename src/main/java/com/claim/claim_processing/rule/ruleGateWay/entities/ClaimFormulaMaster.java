package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_FORMULA_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CLAIM_FORMULA_CODE_VERSION", columnNames = { "FORMULA_CODE", "VERSION_NO" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimFormulaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FORMULA_CODE", nullable = false, length = 100)
    private String formulaCode;

    @Column(name = "FORMULA_NAME", nullable = false, length = 200)
    private String formulaName;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Lob
    @Column(name = "EXPRESSION_TEXT", nullable = false)
    private String expressionText;

    @Column(name = "OUTPUT_VARIABLE_CODE", nullable = false, length = 100)
    private String outputVariableCode;

    @Column(name = "RETURN_TYPE", length = 50)
    @Builder.Default
    private String returnType = "NUMBER";

    @Column(name = "VERSION_NO")
    @Builder.Default
    private Long versionNo = 1L;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private String isActive = "Y";

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (this.returnType == null) {
            this.returnType = "NUMBER";
        }
        if (this.versionNo == null) {
            this.versionNo = 1L;
        }
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
