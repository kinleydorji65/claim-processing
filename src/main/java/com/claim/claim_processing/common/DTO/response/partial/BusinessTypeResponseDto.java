package com.claim.claim_processing.common.DTO.response.partial;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}