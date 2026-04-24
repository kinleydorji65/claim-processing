package com.claim.claim_processing.common.DTO.response.agency;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyCategoryResponseDto {

    private String categoryId;

    private String agencyCategoryCode;

    private String categoryName;

    private String status;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}