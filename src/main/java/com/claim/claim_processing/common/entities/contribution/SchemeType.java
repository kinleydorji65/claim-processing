package com.claim.claim_processing.common.entities.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnumConverter;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SCHEME_TYPES", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEME_TYPE_ID")
    private Long id;

    @Column(name = "SCHEME_TYPE_CODE", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "SCHEME_TYPE_NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "STATUS", length = 1)
    @Builder.Default
    private String isActive = "A";

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