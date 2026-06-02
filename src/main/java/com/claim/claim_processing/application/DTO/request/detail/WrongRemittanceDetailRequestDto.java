package com.claim.claim_processing.application.DTO.request.detail;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemittanceDetailRequestDto {

    private Long claimApplicationId;

    private String agencyCode;

    private Long wrongRemittanceReasonId;

    private Integer remittanceMonth;

    private Integer remittanceYear;

    private String remittanceReferenceNumber;

    private String scheduleNumber;

    private String receiptNumber;

    private String transactionReferenceNumber;

    private LocalDate postingDate;

    private Long contributionTypeId;

    private Long affectedAccountTypeId;

    private Integer affectedMemberCount;

    private Long errorTypeId;

    private Long payeeTypeId;

    private BigDecimal totalRemittedAmount;

    private BigDecimal refundRequestedAmount;

    private String createdBy;

    private String updatedBy;
}