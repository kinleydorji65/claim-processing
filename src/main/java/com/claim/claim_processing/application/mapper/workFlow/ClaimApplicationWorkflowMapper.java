package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClaimApplicationWorkflowMapper {

    public ClaimApplicationWorkflowResponseDto toResponse(
            ClaimApplicationWorkflow entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationWorkflowResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getId()
                                : null
                )
                .applicationNumber(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getApplicationNumber()
                                : null
                )

                .workflowLevel(entity.getWorkflowLevel())

                .workflowStageId(
                        entity.getWorkflowStage() != null
                                ? entity.getWorkflowStage().getId()
                                : null
                )
                .workflowStageName(
                        entity.getWorkflowStage() != null
                                ? entity.getWorkflowStage().getName()
                                : null
                )

                .fromStatusId(
                        entity.getFromStatus() != null
                                ? entity.getFromStatus().getStatusId()
                                : null
                )
                .fromStatusName(
                        entity.getFromStatus() != null
                                ? entity.getFromStatus().getStatusName()
                                : null
                )

                .toStatusId(
                        entity.getToStatus() != null
                                ? entity.getToStatus().getStatusId()
                                : null
                )
                .toStatusName(
                        entity.getToStatus() != null
                                ? entity.getToStatus().getStatusName()
                                : null
                )

                .actionId(
                        entity.getAction() != null
                                ? entity.getAction().getId()
                                : null
                )
                .actionName(
                        entity.getAction() != null
                                ? entity.getAction().getName()
                                : null
                )

                .decisionId(
                        entity.getDecision() != null
                                ? entity.getDecision().getId()
                                : null
                )
                .decisionName(
                        entity.getDecision() != null
                                ? entity.getDecision().getName()
                                : null
                )

                .returnReason(entity.getReturnReason())
                .rejectionReason(entity.getRejectionReason())

                .approvalReasonId(
                        entity.getApprovalReason() != null
                                ? entity.getApprovalReason().getId()
                                : null
                )
                .approvalReasonName(
                        entity.getApprovalReason() != null
                                ? entity.getApprovalReason().getName()
                                : null
                )

                .actionBy(entity.getActionBy())

                .actionAt(
                        entity.getActionAt() != null
                                ? entity.getActionAt().toLocalDateTime()
                                : null
                )

                .officeId(
                        entity.getOffice() != null
                                ? entity.getOffice().getId()
                                : null
                )
                .officeName(
                        entity.getOffice() != null
                                ? entity.getOffice().getName()
                                : null
                )

                .referenceNumber(entity.getReferenceNumber())
                .remarks(entity.getRemarks())

                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
                                : null
                )

                .updatedAt(
                        entity.getUpdatedAt() != null
                                ? entity.getUpdatedAt().toLocalDateTime()
                                : null
                )

                .build();
    }

    public List<ClaimApplicationWorkflowResponseDto> toResponseList(
            List<ClaimApplicationWorkflow> entities
    ) {

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}