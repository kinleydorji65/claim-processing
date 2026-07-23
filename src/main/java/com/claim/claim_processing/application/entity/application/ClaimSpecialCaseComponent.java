package com.claim.claim_processing.application.entity.application;

import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE_COMPONENT", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClaimSpecialCaseComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_APPLICATION_ID", nullable = false)
    private ClaimSpecialCaseApplication specialCaseApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPONENT_MASTER_ID")
    private ComponentMaster componentMaster;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = "Y";
        }
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
