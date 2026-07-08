package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "UNCLAIMED_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnclaimedMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, precision = 19)
    private Long id;

    @Column(name = "DURATION")
    private Integer duration;

    @Column(name = "IS_ACTIVE", columnDefinition = "CHAR(1)")
    @Builder.Default
    private String isActive = "A";

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 255)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
