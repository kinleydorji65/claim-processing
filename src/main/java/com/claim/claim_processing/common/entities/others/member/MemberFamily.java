package com.claim.claim_processing.common.entities.others.member;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_FAMILY", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFamily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "NATIONALITY_ID")
    private Long nationalityId;

    @Column(name = "IDENTITY_TYPE_ID")
    private Long identityTypeId;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    // @Column(name = "MEMBER_CODE")
    // private String memberCode;

    @Column(name = "PHONE_NUMBER", length = 15)
    private Long phoneNumber;

    @Column(name = "RELATION_ID")
    private Long relationId;

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @Column(name = "CREATED_AT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = false)
    private MemberDetail member;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
