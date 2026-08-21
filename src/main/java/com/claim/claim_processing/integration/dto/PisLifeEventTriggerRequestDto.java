package com.claim.claim_processing.integration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request body for pension-service's POST /api/pension/auto-trigger/pis-life-event.
 *
 * Field names must match nppf.org.bt.pension_service.dto.request.AutoTriggerPisLifeEventRequestDTO
 * exactly (verified against the live pension-service source):
 *   - deceasedMemberCode (required, NotBlank)
 *   - agencyCode         (required, NotBlank)
 *   - dateOfDeath        (required, NotNull, must be in the past)
 *   - pisEventReference  (required, NotBlank) — used as the correlation/idempotency key
 *   - remarks            (optional)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PisLifeEventTriggerRequestDto {
    private String deceasedMemberCode;
    private String agencyCode;
    private LocalDate dateOfDeath;
    private String pisEventReference;
    private String remarks;
}
