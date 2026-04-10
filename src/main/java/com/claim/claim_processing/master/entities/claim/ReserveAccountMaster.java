package com.claim.claim_processing.master.entities.claim;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "ACCOUNT_TYPE_CODE", nullable = false, length = 40)
    private String accountTypeCode;

    @Column(name = "SCHEME_TYPE_CODE", length = 30)
    private String schemeTypeCode;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
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
        if (isActive == null) {
            isActive = "Y";
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}