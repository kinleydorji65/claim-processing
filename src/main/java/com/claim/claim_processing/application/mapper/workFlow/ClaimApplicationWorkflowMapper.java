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

                .fromStageId(
                        entity.getFromStage() != null
                                ? entity.getFromStage().getId()
                                : null
                )
                .fromStageName(
                        entity.getFromStage() != null
                                ? entity.getFromStage().getName()
                                : null
                )

                .toStageId(
                        entity.getToStage() != null
                                ? entity.getToStage().getId()
                                : null
                )
                .toStageName(
                        entity.getToStage() != null
                                ? entity.getToStage().getName()
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
                .reason(
                        entity.getReason() != null
                                ? entity.getReason()
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

                .actionBy(entity.getActionBy())
                .actionAt(
                        entity.getActionAt() != null
                                ? entity.getActionAt().toLocalDateTime()
                                : null
                )

                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
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