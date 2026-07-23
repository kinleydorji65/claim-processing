package com.claim.claim_processing.integration.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.claim.claim_processing.integration.dto.PensionAutoTriggerRequestDto;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerResponseDto;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerApiResponseWrapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class PensionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.pension.base-url}")
    private String baseUrl;

    @Value("${app.pension.endpoints.auto-trigger-claim-approved:/api/pension/auto-trigger/pf-claim-approved}")
    private String autoTriggerPath;

    public PensionAutoTriggerResponseDto triggerPfClaimApproved(PensionAutoTriggerRequestDto request) {
        String url = baseUrl + autoTriggerPath;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<PensionAutoTriggerApiResponseWrapper> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    PensionAutoTriggerApiResponseWrapper.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null || res.getBody().getData() == null) {
                log.error("Pension-service auto-trigger returned {} with no body for claim {}",
                        res.getStatusCode(), request.getPfSettlementClaimId());
                return null;
            }
            return res.getBody().getData();

        } catch (ResourceAccessException ex) {
            log.error("Pension-service unreachable during auto-trigger for claim {}: {}",
                    request.getPfSettlementClaimId(), ex.getMessage());
            return null;
        } catch (HttpStatusCodeException ex) {
            log.error("Pension-service rejected auto-trigger for claim {}. Status: {}, Body: {}",
                    request.getPfSettlementClaimId(), ex.getStatusCode(), ex.getResponseBodyAsString());
            return null;
        } catch (RestClientException ex) {
            log.error("Unexpected error calling pension-service auto-trigger for claim {}: {}",
                    request.getPfSettlementClaimId(), ex.getMessage(), ex);
            return null;
        }
    }
}