package com.claim.claim_processing.integration.client;

import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerApiResponseWrapper;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerRequestDto;
import com.claim.claim_processing.integration.dto.PensionAutoTriggerResponseDto;
import com.claim.claim_processing.integration.dto.PisLifeEventTriggerRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PensionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.pension.base-url}")
    private String baseUrl;

    @Value("${app.pension.endpoints.auto-trigger-claim-approved:/api/pension/auto-trigger/pf-claim-approved}")
    private String autoTriggerPath;

    // Configurable path for the survivor in-service-death trigger, same pattern as above.
    @Value("${app.pension.endpoints.auto-trigger-pis-life-event:/api/pension/auto-trigger/pis-life-event}")
    private String pisLifeEventPath;

    // ✅ Create a properly configured ObjectMapper for serialization
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public PensionAutoTriggerResponseDto triggerPfClaimApproved(PensionAutoTriggerRequestDto request) {
        String url = baseUrl + autoTriggerPath;

        try {
            // ✅ Serialize the request to JSON manually
            String requestJson = objectMapper.writeValueAsString(request);
            
            log.info("=== SENDING PENSION AUTO-TRIGGER REQUEST ===");
            log.info("URL: {}", url);
            log.info("FULL JSON REQUEST BODY: {}", requestJson);
            log.info("======================================");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ Send the JSON string directly
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            
            ResponseEntity<PensionAutoTriggerApiResponseWrapper> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PensionAutoTriggerApiResponseWrapper.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw ClaimException.internalError("Pension service auto-trigger failed");
            }

            PensionAutoTriggerApiResponseWrapper body = res.getBody();

            if (!body.isSuccess() || body.getData() == null) {
                throw ClaimException.unprocessable(
                        "Pension auto-trigger failed: " + (body.getMessage() != null ? body.getMessage() : "No message")
                );
            }

            log.info("✅ Pension service response: {}", res.getBody());
            return body.getData();

        } catch (ResourceAccessException ex) {
            throw ClaimException.internalError("Pension service is unreachable while auto-triggering", ex);
        } catch (HttpStatusCodeException ex) {
            String responseBody = ex.getResponseBodyAsString();

            if (ex.getStatusCode().is4xxClientError()) {
                throw ClaimException.badRequest(
                        "Pension service rejected request for auto-trigger. Status: "
                                + ex.getStatusCode()
                                + ", Body: "
                                + responseBody
                );
            }

            throw ClaimException.internalError(
                    "Pension service error while auto-triggering. Status: "
                            + ex.getStatusCode()
                            + ", Body: "
                            + responseBody,
                    ex
            );
        } catch (RestClientException ex) {
            throw ClaimException.internalError("Unexpected error while calling pension service for auto-trigger", ex);
        } catch (Exception ex) {
            throw ClaimException.internalError("Error serializing request: " + ex.getMessage(), ex);
        }
    }

    public PensionAutoTriggerResponseDto triggerPisLifeEvent(PisLifeEventTriggerRequestDto request) {
        String url = baseUrl + pisLifeEventPath;

        try {
            String requestJson = objectMapper.writeValueAsString(request);

            log.info("=== SENDING PIS LIFE-EVENT AUTO-TRIGGER REQUEST ===");
            log.info("URL: {}", url);
            log.info("FULL JSON REQUEST BODY: {}", requestJson);
            log.info("======================================");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<PensionAutoTriggerApiResponseWrapper> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PensionAutoTriggerApiResponseWrapper.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw ClaimException.internalError("Pension service pis-life-event auto-trigger failed");
            }

            PensionAutoTriggerApiResponseWrapper body = res.getBody();

            if (!body.isSuccess() || body.getData() == null) {
                throw ClaimException.unprocessable(
                        "Pension pis-life-event auto-trigger failed: "
                                + (body.getMessage() != null ? body.getMessage() : "No message")
                );
            }

            log.info("✅ Pension service response: {}", res.getBody());
            return body.getData();

        } catch (ResourceAccessException ex) {
            throw ClaimException.internalError(
                    "Pension service is unreachable while auto-triggering pis-life-event", ex);
        } catch (HttpStatusCodeException ex) {
            String responseBody = ex.getResponseBodyAsString();

            if (ex.getStatusCode().is4xxClientError()) {
                throw ClaimException.badRequest(
                        "Pension service rejected pis-life-event request. Status: "
                                + ex.getStatusCode()
                                + ", Body: "
                                + responseBody
                );
            }

            throw ClaimException.internalError(
                    "Pension service error during pis-life-event auto-trigger. Status: "
                            + ex.getStatusCode()
                            + ", Body: "
                            + responseBody,
                    ex
            );
        } catch (RestClientException ex) {
            throw ClaimException.internalError(
                    "Unexpected error while calling pension service for pis-life-event", ex);
        } catch (Exception ex) {
            throw ClaimException.internalError("Error serializing pis-life-event request: " + ex.getMessage(), ex);
        }
    }
}