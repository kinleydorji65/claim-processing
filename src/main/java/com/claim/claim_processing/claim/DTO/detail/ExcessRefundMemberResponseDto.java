package com.claim.claim_processing.claim.DTO.detail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcessRefundMemberResponseDto {
    private Long id;

    // ---------- Member Info ----------
    private String memberCode;
    private String memberNppfNumber;

    // ---------- Master References ----------
    private Long schemeTypeId;
    private String schemeTypeName;

    private String employmentType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;

    private Long currentStatus;

    private Long payeeTypeId;
    private String payeeTypeName;

    // ---------- Financial Details ----------
    private BigDecimal scheduledContribution;
    private BigDecimal actualContribution;
    private BigDecimal calculatedExcess;
    private BigDecimal requestedRefundAmount;

    private BigDecimal totalPaid;
    private BigDecimal totalContribution;
    private BigDecimal totalExcess;

    private BigDecimal memberRequestedRefund;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
