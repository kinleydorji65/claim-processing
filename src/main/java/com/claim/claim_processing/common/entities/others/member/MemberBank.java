package com.claim.claim_processing.common.entities.others.member;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_BANK_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "ACCOUNT_NUMBER", nullable = true, length = 50)
    private String actNumber;
    
    @Column(name = "ACCOUNT_HOLDER_NAME", nullable = true, length = 200)
    private String holderName;
    
    @Column(name = "ACCOUNT_TYPE", nullable = true, length = 20)
    private String accountType;

    @Column(name = "BANK_ID", nullable = true)
    private Long bankId;
    
    @Column(name = "IS_DEFAULT", nullable = true)
    @Builder.Default
    private Boolean isDefault = false;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = true)
    private MemberDetail member;
}
