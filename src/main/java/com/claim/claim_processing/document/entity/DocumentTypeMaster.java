package com.claim.claim_processing.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Entity
@Table(name = "DOCUMENT_TYPE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_DOCUMENT_TYPE_CODE", columnNames = "CODE")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTypeMaster {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @Column(name = "CODE", nullable = false, length = 50)
        private String code;

        @Column(name = "NAME", nullable = false, length = 150)
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DOCUMENT_CATEGORY_ID", foreignKey = @ForeignKey(name = "FK_DOC_TYPE_CATEGORY"))
        private DocumentCategoryMaster documentCategory;

        @Column(name = "DESCRIPTION", length = 300)
        private String description;

        @Column(name = "IS_MANDATORY", length = 1)
        @Builder.Default
        private String isMandatory = "N";

        @Column(name = "IS_MULTIPLE_ALLOWED", length = 1)
        @Builder.Default
        private String isMultipleAllowed = "N";

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
