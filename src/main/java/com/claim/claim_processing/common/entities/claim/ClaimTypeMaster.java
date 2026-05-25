package com.claim.claim_processing.common.entities.claim;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnumConverter;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_TYPE_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
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

    @Convert(converter = ActivityEnumConverter.class)
    @Column(name = "IS_ACTIVE", length = 1)
    private ActivityEnum isActive;

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