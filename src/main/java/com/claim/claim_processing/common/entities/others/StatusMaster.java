package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "status_master", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
public class StatusMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "statuse_name", nullable = false, length = 100)
    private String statuseName;
}