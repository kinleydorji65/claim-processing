package com.claim.claim_processing.rule.ruleGateWay.entities.rule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;

@Entity
@Table(
        name = "CATEGORY_SCHEME_MAPPING",
        schema = "PPFMS_MASTER_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CATEGORY_SCHEME_MAPPING",
                        columnNames = {"CATEGORY_ID", "SCHEME_TYPE_ID"}
                ),
                @UniqueConstraint(
                        name = "UK_CATEGORY_SCHEME_CODE",
                        columnNames = {"CATEGORY_CODE", "SCHEME_CODE"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySchemeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            nullable = false
    )
    private AgencyCategory agencyCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "SCHEME_TYPE_ID",
            referencedColumnName = "SCHEME_TYPE_ID",
            nullable = false
    )
    private SchemeType schemeType;

    @Column(name = "CATEGORY_CODE", nullable = false, length = 50)
    private String categoryCode;
    @Column(name = "CATEGORY_SCHEME_CODE", nullable = false, length = 50)
    private String categorySchemeCode;

    @Column(name = "SCHEME_CODE", nullable = false, length = 50)
    private String schemeCode;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
