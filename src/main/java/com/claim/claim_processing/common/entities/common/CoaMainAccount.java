package com.claim.claim_processing.common.entities.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "COA_MAIN_ACCOUNT", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoaMainAccount {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNT_CODE")
    private String accountCode;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    @Column(name = "NORMAL_DRCR")
    private String normalDrcr;

    @Column(name = "SUB_ACCOUNT_SOURCE")
    private String subAccountSource;

    @Column(name = "FAD_APPROVED")
    private String fadApproved;

    @Column(name = "IS_ACTIVE")
    private String isActive;
}
