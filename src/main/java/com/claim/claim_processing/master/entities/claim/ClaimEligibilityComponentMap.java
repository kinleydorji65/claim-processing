package com.claim.claim_processing.master.entities.claim;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_ELIGIBILITY_COMPONENT_MAP", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEligibilityComponentMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_MAP_RULE"))
    private ClaimEligibilityMaster claimEligibilityMaster;

    @Column(name = "BENEFIT_TYPE", length = 50)
    private String benefitType;

    @Column(name = "COMPONENT_CODE", length = 30)
    private String componentCode;

    @Column(name = "STATUS", length = 30)
    private String status;

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
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}