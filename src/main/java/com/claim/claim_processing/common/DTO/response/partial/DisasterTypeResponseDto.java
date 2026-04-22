package com.claim.claim_processing.common.DTO.response.partial;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String isActive;

    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
}