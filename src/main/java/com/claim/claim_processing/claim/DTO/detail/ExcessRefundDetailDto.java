package com.claim.claim_processing.claim.DTO.detail;

import com.claim.claim_processing.claim.entity.applicationEnum.MemberRefundScope;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcessRefundDetailDto {

    private Long id;

    private Long claimApplicationId;

    // ---------- Child List ----------
    private List<ExcessRefundMemberResponseDto> excessRefundMemberDetails;

    // ---------- Walk-in Info ----------
    private String walkInReceivedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate walkInReceivedDate;

    // ---------- Masters ----------
    private Long refundScopeId;
    private String refundScopeName;

    private MemberRefundScope memberRefundScope;

    private Long payeeTypeId;
    private String payeeTypeName;

    private Long excessRefundReasonId;
    private String excessRefundReasonName;

    // ---------- Payment / Business ----------
    private String remarksJustification;

    private Integer paymentMonth;
    private Integer paymentYear;

    private String receiptNumber;
    private String paymentReferenceNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    private String transactionNumber;
    private String bankReference;

    private String contributionScheduleReference;

    private Integer uploadedRemittanceMonth;
    private Integer uploadedRemittanceYear;

    private String appliedBy;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
