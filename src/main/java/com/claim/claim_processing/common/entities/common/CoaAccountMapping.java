package com.claim.claim_processing.common.entities.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "COA_ACCOUNT_MAPPING", schema = "PPFMS_Master_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoaAccountMapping {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "COMPONENT_CODE")
    private String componentCode;

    @Column(name = "ENTRY_ROLE")
    private String entryRole;

    @Column(name = "TRAN_CODE")
    private String tranCode;

    @Column(name = "DRCR")
    private String drcr;

    @Column(name = "MAIN_ACCOUNT_CODE")
    private String mainAccountCode;

    @Column(name = "SUB_ACCOUNT_CODE")
    private String subAccountCode;

    @Column(name = "SEQ_NO")
    private Integer seqNo;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Transient
    private String mainAccountName;

    @Transient
    private String subAccountName;
}
