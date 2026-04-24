package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "IDENTITY_TYPES", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;
}
