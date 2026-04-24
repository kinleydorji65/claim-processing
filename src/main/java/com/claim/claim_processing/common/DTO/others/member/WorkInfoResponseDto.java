package com.claim.claim_processing.common.DTO.others.member;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkInfoResponseDto {

    private Long id;

    // -------------------------------
    // SERVICE DETAILS
    // -------------------------------
    private Date serviceJoiningDate;

    private Long employmentTypeId;
    private String employmentTypeName; // optional (master)

    // -------------------------------
    // FINANCIAL
    // -------------------------------
    private BigDecimal basicPay;

    // -------------------------------
    // MEMBER (FLATTENED)
    // -------------------------------
    private String memberCode;
    private String memberName; // optional
}
