package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;

@Component
public class ClaimApplicationForfeitedComponentResponseMapper {

    public ClaimApplicationForfeitedComponentResponseDto toResponse(
            ClaimApplicationForfeitedComponent entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationForfeitedComponentResponseDto.builder()

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

                .componentCode(entity.getComponentCode())
                .componentName(entity.getComponentName())
                .componentType(entity.getComponentType())

                .amount(entity.getAmount())

                .reason(entity.getReason())

                .ruleCode(entity.getRuleCode())
                .subClaimCode(entity.getSubClaimCode())

                .isActive(entity.getIsActive())

                .createdBy(entity.getCreatedBy())

                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
                                : null
                )

                .updatedBy(entity.getUpdatedBy())

                .updatedAt(
                        entity.getUpdatedAt() != null
                                ? entity.getUpdatedAt().toLocalDateTime()
                                : null
                )

                .build();
    }

    public List<ClaimApplicationForfeitedComponentResponseDto> toResponseList(
            List<ClaimApplicationForfeitedComponent> entities
    ) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
