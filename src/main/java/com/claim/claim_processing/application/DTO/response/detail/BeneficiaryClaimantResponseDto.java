package com.claim.claim_processing.application.DTO.response.detail;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryClaimantResponseDto {

    private Long id;

    private Long beneficiarySettlementDetailId;

    // ---------- Relationships ----------
    private Long nomineeId;
    private String nomineeFirstName;
    private String nomineeMiddleName;
    private String nomineeLastName;

    private Long dependentId;
    private String dependentFirstName;
    private String dependentMiddleName;
    private String dependentLastName;

    private Long claimantTypeId;
    private String claimantTypeName;

    private Long payeeTypeId;
    private String payeeTypeName;

    private String beneficiaryIdentifier;

    private Long relationshipTypeId;
    private String relationshipTypeName;

    private String beneficiaryName;

    private LocalDate dateOfBirth;

    private BigDecimal beneficiarySharePercentage;

    // ---------- Business Fields ----------
    private Integer priorityOrder;

    private ActivityEnum isMemberFamily;
    private ActivityEnum isMinor;

    private String guardianName;
    private String guardianIdentifier;

    private BigDecimal benefitAmount;

    private ActivityEnum isEligible;
    private ActivityEnum isSelected;

    private String remarks;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
