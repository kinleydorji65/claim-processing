package com.claim.claim_processing.common.entities.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.contribution.SchemeMaster;

@Entity
@Table(name = "RESERVE_ACCOUNT_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveAccountMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RESERVE_ACCOUNT_CODE", nullable = false, unique = true, length = 50)
    private String reserveAccountCode;

    @Column(name = "RESERVE_ACCOUNT_NAME", nullable = false, length = 150)
    private String reserveAccountName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ACCOUNT_TYPE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_RESERVE_ACCOUNT_TYPE")
    )
    private AccountTypeMaster accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "SCHEME_TYPE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_RESERVE_ACCOUNT_SCHEME")
    )
    private SchemeMaster schemeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

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
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}