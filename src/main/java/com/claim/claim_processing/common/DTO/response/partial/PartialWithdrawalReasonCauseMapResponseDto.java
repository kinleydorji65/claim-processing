package com.claim.claim_processing.common.DTO.response.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialWithdrawalReasonCauseMapResponseDto {

    private Long id;

    // -----------------------
    // FK INFO
    // -----------------------
    private PartialWithdrawalReasonResponseDto reason;

    private PartialWithdrawalCauseResponseDto cause;

    // -----------------------
    // STATUS
    // -----------------------
    private ActivityEnum isActive;

    // -----------------------
    // AUDIT
    // -----------------------
    private String createdBy;
    private Timestamp createdAt;

    private String updatedBy;
    private Timestamp updatedAt;
}