package com.claim.claim_processing.common.DTO.others;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StatusDTO {
    private Long statusId;
    private String statuseName;   // ⚠ matches response spelling
    private LocalDateTime createdAt;
}
