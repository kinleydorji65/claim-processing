package com.claim.claim_processing.common.entities.others.member;

import java.sql.Date;

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

    @Column(name = "IDENTITY_TYPE_ID")
    private Long identityTypeId;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    @Column(name = "RELATION_ID")
    private Long relationId;

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = false)
    private MemberDetail member;
}
