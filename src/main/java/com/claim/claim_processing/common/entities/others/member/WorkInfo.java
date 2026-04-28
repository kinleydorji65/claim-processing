package com.claim.claim_processing.common.entities.others.member;

import java.math.BigDecimal;
import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_WORK_INFO", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WorkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SERVICE_JOINING_DATE")
    private Date serviceJoiningDate;

    @Column(name = "EMPLOYMENT_TYPE_ID")
    private Long employmentTypeId;

    @Column(name = "BASIC_PAY", precision = 12, scale = 2)
    private BigDecimal basicPay;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = false)
    private MemberDetail member;
}
