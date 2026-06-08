package com.claim.claim_processing.application.DTO.request.workFlow;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationWorkflowRequestDto {
    private Long fromStageId;
    private Long toStageId;
    private Long fromStatusId;
    private Long toStatusId;
    private Long actionId;
    private String reason;
    private Long officeId;
    private String actionBy;
}