package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_TYPE", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_TYPE_ID")
    private Long memberTypeId;

    @Column(name = "MEMBER_TYPE_NAME")
    private String memberTypeName;
}
