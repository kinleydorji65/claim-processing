package com.claim.claim_processing.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Entity
@Table(name = "DOCUMENT_CATEGORY_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_DOCUMENT_CATEGORY_CODE", columnNames = "CODE")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCategoryMaster {

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

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT", insertable = false, updatable = false)
        private Timestamp updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @PrePersist
        public void prePersist() {
                this.createdAt = new Timestamp(System.currentTimeMillis());
                this.updatedAt = this.createdAt;
                if (this.isActive == null) {
                        this.isActive = ActivityEnum.Y;
                }
        }

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = new Timestamp(System.currentTimeMillis());
        }
}
