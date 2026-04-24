package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RELATION_TYPES", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RELATION_TYPE_ID")
    private Long relationTypeId;

    @Column(name = "RELATION_TYPE_NAME")
    private String relationTypeName;
}
