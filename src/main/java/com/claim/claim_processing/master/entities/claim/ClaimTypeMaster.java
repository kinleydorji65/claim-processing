package com.claim.claim_processing.master.entities.claim;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLAIM_TYPE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "CATEGORY_CODE", length = 50)
    private String categoryCode;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = "Y";
        }
    }
}