package com.claim.claim_processing.document.entity;

import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "DOCUMENT_MASTER_MAP",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_DOCUMENT_MASTER_MAP",
                        columnNames = {
                                "DOCUMENT_ID",
                                "CLAIM_TYPE_ID"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMasterMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DOCUMENT_ID",
            nullable = false
    )
    private DocumentTypeMaster document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_TYPE_ID",
            nullable = false
    )
    private ClaimTypeMaster claimType;
}