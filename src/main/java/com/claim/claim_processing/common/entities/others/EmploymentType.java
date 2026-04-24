package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EMPLOYMENT_TYPE", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYMENT_TYPE_ID")
    private Long employmentTypeId;

    @Column(name = "EMPLOYMENT_TYPE_NAME", nullable = false, length = 200)
    private String employmentTypeName;
}
