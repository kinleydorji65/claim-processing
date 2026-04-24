package com.claim.claim_processing.common.DTO.response.specialCase;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialCaseAuthorityResponseDto {

    private Long id;
    private String code;
    private String name;
    private ActivityEnum isActive;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
