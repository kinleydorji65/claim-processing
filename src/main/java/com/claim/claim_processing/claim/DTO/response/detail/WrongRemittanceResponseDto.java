package com.claim.claim_processing.claim.DTO.response.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemittanceResponseDto {

    private Long id;

    private Long claimApplicationId;

    // ---------- Basic Info ----------
    private String agencyCode;

    // ---------- Masters ----------
    private Long wrongRemittanceReasonId;
    private String wrongRemittanceReasonName;

    private Long contributionTypeId;
    private String contributionTypeName;

    private Long affectedAccountTypeId;
    private String affectedAccountTypeName;

    private Long errorTypeId;
    private String errorTypeName;

    private Long payeeTypeId;
    private String payeeTypeName;

    // ---------- Remittance Info ----------
    private Integer remittanceMonth;
    private Integer remittanceYear;

    private String remittanceReferenceNumber;
    private String scheduleNumber;
    private String receiptNumber;
    private String transactionReferenceNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate postingDate;

    // ---------- Impact ----------
    private Integer affectedMemberCount;

    // ---------- Financial ----------
    private BigDecimal totalRemittedAmount;
    private BigDecimal refundRequestedAmount;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
