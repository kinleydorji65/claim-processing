package com.claim.claim_processing.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.status_master.VerificationStatusMaster;

@Entity
@Table(name = "CLAIM_APPLICATION_DOCUMENT_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationDocumentDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CADD_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @Column(name = "BENEFICIARY_IDENTIFIER", length = 100)
        private String beneficiaryIdentifier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DOCUMENT_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CADD_DOC_TYPE"))
        private DocumentTypeMaster documentType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DOCUMENT_CATEGORY_ID", foreignKey = @ForeignKey(name = "FK_CADD_DOC_CATEGORY"))
        private DocumentCategoryMaster documentCategory;

        @Column(name = "DOCUMENT_NAME", length = 255)
        private String documentName;

        @Column(name = "DOCUMENT_REFERENCE_NUMBER", length = 100)
        private String documentReferenceNumber;

        @Column(name = "FILE_NAME", length = 255)
        private String fileName;

        @Column(name = "FILE_PATH", length = 500)
        private String filePath;

        @Column(name = "FILE_URL", length = 500)
        private String fileUrl;

        @Column(name = "FILE_EXTENSION", length = 20)
        private String fileExtension;

        @Column(name = "FILE_SIZE")
        private Long fileSize;

        @Column(name = "MIME_TYPE", length = 100)
        private String mimeType;

        @Column(name = "IS_MANDATORY", length = 1)
        @Builder.Default
        private String isMandatory = "N";

        @Column(name = "IS_VERIFIED", length = 1)
        @Builder.Default
        private String isVerified = "N";

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "VERIFICATION_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CADD_VER_STATUS"))
        private VerificationStatusMaster verificationStatus;

        @Column(name = "DOCUMENT_REMARKS", length = 1000)
        private String documentRemarks;

        @Column(name = "UPLOADED_BY", length = 100)
        private String uploadedBy;

        @Column(name = "UPLOADED_AT")
        private Timestamp uploadedAt;

        @Column(name = "VERIFIED_BY", length = 100)
        private String verifiedBy;

        @Column(name = "VERIFIED_AT")
        private Timestamp verifiedAt;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "UPDATED_AT", insertable = false, updatable = false)
        private Timestamp updatedAt;

        @PrePersist
        public void prePersist() {
                createdAt = new Timestamp(System.currentTimeMillis());
                updatedAt = new Timestamp(System.currentTimeMillis());
        }

        @PreUpdate
        public void preUpdate() {
                updatedAt = new Timestamp(System.currentTimeMillis());
        }
}
