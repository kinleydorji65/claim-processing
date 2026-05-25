package com.claim.claim_processing.common.entities.adjustmentMaster;

import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(
        name = "RENTAL_ADJUSTMENT_MASTER",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_RENTAL_ADJUSTMENT_NAME",
                        columnNames = "NAME"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalAdjustmentMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "RULE_TYPE_ID",
            nullable = false
    )
    private RuleTypeMaster ruleType;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "PERCENTAGE", precision = 10, scale = 2)
    private BigDecimal percentage;

    @Column(name = "PRIORITY_ORDER")
    private Integer priorityOrder;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
