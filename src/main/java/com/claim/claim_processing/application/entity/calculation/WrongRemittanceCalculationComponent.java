package com.claim.claim_processing.application.entity.calculation;

import com.claim.claim_processing.application.entity.detail.WrongRemitance;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "WRONG_REMITTANCE_CALCULATION_COMPONENT", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrongRemittanceCalculationComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ===== FOREIGN KEY TO WRONG REMITANCE =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRONG_REMITANCE_ID", nullable = false)
    private WrongRemitance wrongRemitance;

    // ===== REFERENCE TO COMPONENT MASTER =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPONENT_MASTER_ID", referencedColumnName = "ID", nullable = false)
    private ComponentMaster componentMaster;

    // ===== AMOUNT =====
    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;


    // ===== AUDIT FIELDS =====
    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
