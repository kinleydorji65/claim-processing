package com.claim.claim_processing.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PensionAutoTriggerApiResponseWrapper {
    private boolean success;
    private String message;
    private PensionAutoTriggerResponseDto data;
}