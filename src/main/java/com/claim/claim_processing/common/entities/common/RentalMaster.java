package com.claim.claim_processing.common.entities.common;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "RENTAL_MASTER",
        schema = "PPFMS_MASTER_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_RENTAL_MASTER_TYPE",
                        columnNames = "RENTAL_TYPE"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RENTAL_TYPE", nullable = false, length = 100)
    private String rentalType;

    @Column(name = "DESCRIPTION", length = 255)
    private String rentalDesc;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;
}