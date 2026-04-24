package com.claim.claim_processing.common.entities.refundMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(
        name = "EXCESS_REFUND_REASON_MASTER",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_EXCESS_REFUND_REASON_CODE", columnNames = "CODE")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcessRefundReasonMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "DESCRIPTION", length = 300)
    private String description;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

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
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
        if (isActive == null) {
            isActive = ActivityEnum.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
