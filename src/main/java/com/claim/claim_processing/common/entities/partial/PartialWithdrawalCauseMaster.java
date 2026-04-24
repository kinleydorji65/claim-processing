package com.claim.claim_processing.common.entities.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "PARTIAL_WITHDRAWAL_CAUSE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
                @UniqueConstraint(name = "UK_PARTIAL_WITHDRAWAL_CAUSE_CODE", columnNames = { "CODE" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalCauseMaster {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "CODE", nullable = false, length = 50)
        private String code;

        @Column(name = "NAME", nullable = false, length = 150)
        private String name;

        @Column(name = "DESCRIPTION", length = 500)
        private String description;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "UPDATED_AT", insertable = false)
        private Timestamp updatedAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @PrePersist
        public void prePersist() {
                if (this.isActive == null) {
                        this.isActive = ActivityEnum.Y;
                }
                this.createdAt = new Timestamp(System.currentTimeMillis());
                this.updatedAt = new Timestamp(System.currentTimeMillis());
        }

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = new Timestamp(System.currentTimeMillis());
        }
}
