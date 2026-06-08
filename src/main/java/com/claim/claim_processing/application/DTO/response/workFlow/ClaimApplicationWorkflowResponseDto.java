package com.claim.claim_processing.application.DTO.response.workFlow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationWorkflowResponseDto {

    private Long id;

    // ----------------------------
    // Claim Application
    // ----------------------------
    private Long claimApplicationId;
    private String applicationNumber;

    // ----------------------------
    // Stage Transition
    // ----------------------------
    private Long fromStageId;
    private String fromStageName;

    private Long toStageId;
    private String toStageName;

    // ----------------------------
    // Status Transition
    // ----------------------------
    private Long fromStatusId;
    private String fromStatusName;

    private Long toStatusId;
    private String toStatusName;

    // ----------------------------
    // Action & Decision
    // ----------------------------
    private Long actionId;
    private String actionName;

    private String reason;

    // ----------------------------
    // Office
    // ----------------------------
    private Long officeId;
    private String officeName;

    // ----------------------------
    // Workflow Action
    // ----------------------------
    private String actionBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}