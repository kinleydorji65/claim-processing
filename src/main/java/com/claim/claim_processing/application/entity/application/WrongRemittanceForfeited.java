package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.claim.claim_processing.application.entity.detail.WrongRemitance;

@Entity
@Table(
        name = "WRONG_REMITTANCE_FORFEITED_COMPONENT",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrongRemittanceForfeited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ===== FOREIGN KEY TO WRONG REMITANCE (MANY-TO-ONE) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "WRONG_REMITANCE_ID",
            nullable = false
    )
    private WrongRemitance wrongRemitance;

    @Column(name = "COMPONENT_CODE", length = 50, nullable = false)
    private String componentCode;

    @Column(name = "COMPONENT_NAME", length = 100)
    private String componentName;

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    // ===== AUDIT FIELDS =====
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
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
        if (updatedBy == null) {
            updatedBy = "SYSTEM";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
        if (updatedBy == null) {
            updatedBy = "SYSTEM";
        }
    }
}
