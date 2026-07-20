package com.claim.claim_processing.application.entity.claimDetail;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CLAIM_FORFEITED_COMPONENT",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimForfeitedComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_ID",
            nullable = false
    )
    private ClaimDetail claimDetail;

     @Column(name = "COMPONENT_CODE", length = 50, nullable = false)
    private String componentCode;

    @Column(name = "COMPONENT_NAME", length = 100)
    private String componentName;

    @Column(name = "COMPONENT_TYPE", length = 50)
    private String componentType; // FORFEITED

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "REASON", length = 1000)
    private String reason;

    @Column(name = "RULE_CODE", length = 100)
    private String ruleCode;

    @Column(name = "SUB_CLAIM_CODE", length = 100)
    private String subClaimCode;

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
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
