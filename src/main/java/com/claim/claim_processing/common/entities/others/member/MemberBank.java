package com.claim.claim_processing.common.entities.others.member;

import java.sql.Timestamp;

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

    // @Column(name = "MEMBER_CODE")
    // private String memberCode;
    
    @Column(name = "ACCOUNT_HOLDER_NAME", nullable = true, length = 200)
    private String holderName;
    
    @Column(name = "ACCOUNT_TYPE", nullable = true, length = 20)
    private String accountType;

    @Column(name = "BANK_ID", nullable = true)
    private Long bankId;
    
    @Column(name = "IS_DEFAULT", nullable = true)
    @Builder.Default
    private Boolean isDefault = false;
    
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = true)
    private MemberDetail member;
}
