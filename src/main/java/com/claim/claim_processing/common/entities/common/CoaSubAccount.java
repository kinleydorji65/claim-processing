package com.claim.claim_processing.common.entities.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COA_SUB_ACCOUNT", schema = "PPFMS_Master_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoaSubAccount {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "MAIN_ACCOUNT_ID")
    private Long mainAccountId;

    @Column(name = "SUB_ACCOUNT_CODE")
    private String subAccountCode;

    @Column(name = "SUB_ACCOUNT_NAME")
    private String subAccountName;

    @Column(name = "BALANCE_NATURE")
    private String balanceNature;

    @Column(name = "FAD_APPROVED")
    private String fadApproved;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_ACCOUNT_ID", insertable = false, updatable = false)
    private CoaMainAccount mainAccount;
}
