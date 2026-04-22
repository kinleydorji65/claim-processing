package com.claim.claim_processing.common.DTO.response.special_case;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialCaseAuthorityResponseDto {

    private Long id;
    private String code;
    private String name;
    private String isActive;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}