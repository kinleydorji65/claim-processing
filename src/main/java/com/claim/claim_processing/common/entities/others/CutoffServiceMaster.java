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
@Table(name = "CUTOFF_SERVICE_MASTER", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CutoffServiceMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "number_of_years", nullable = false)
    private Integer numberOfYears;

    @Column(name = "status", nullable = false, length = 1)
    @Builder.Default
    private String status = "Y";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}

