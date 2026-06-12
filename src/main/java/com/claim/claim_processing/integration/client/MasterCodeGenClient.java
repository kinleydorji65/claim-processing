package com.claim.claim_processing.integration.client;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.dto.CodeGenerationResponseDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MasterCodeGenClient {

    private final RestTemplate restTemplate;

    @Value("${app.master.base-url}")
    private String baseUrl;

    @Value("${app.master.endpoints.types:/api/master-data/codes/types}")
    private String typesPath;

    @Value("${app.master.endpoints.generate:/api/master-data/codes/generate}")
    private String generatePath;

    public List<String> getCodeTypes() {
        String url = baseUrl + typesPath;

        try {
            ResponseEntity<List<String>> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(defaultHeaders()),
                    new ParameterizedTypeReference<>() {}
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw ClaimException.internalError("Failed to fetch code types from master service");
            }

            return res.getBody();

        } catch (ResourceAccessException ex) {
            // timeout / connection refused / DNS
            throw ClaimException.internalError("Master service is unreachable while fetching code types", ex);
        } catch (HttpStatusCodeException ex) {
            // master returned non-2xx
            throw ClaimException.internalError(
                    "Master service error while fetching code types. Status: " + ex.getStatusCode(),
                    ex
            );
        } catch (RestClientException ex) {
            throw ClaimException.internalError("Unexpected error while fetching code types from master service", ex);
        }
    }

    public String generateCode(String codeType, String prefix, String agencyAdditionalCode) {

        // Validate input early
        if (codeType == null || codeType.trim().isEmpty()) {
            throw ClaimException.badRequest("codeType is required");
        }
        if (prefix == null || prefix.trim().isEmpty()) {
            throw ClaimException.badRequest("prefix is required");
        }

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + generatePath)
                .queryParam("codeType", codeType)
                .queryParam("prefix", prefix)
                .queryParam("agencyAdditionalCategoryCode", agencyAdditionalCode == null ? null : agencyAdditionalCode)
                .toUriString();

        try {
            ResponseEntity<CodeGenerationResponseDto> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(defaultHeaders()),
                    CodeGenerationResponseDto.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw ClaimException.internalError("Master service code generation failed");
            }

            CodeGenerationResponseDto body = res.getBody();

            if (!body.isSuccess() || body.getGeneratedCode() == null || body.getGeneratedCode().isBlank()) {
                // this is a business failure from master, not a crash
                throw ClaimException.unprocessable(
                        "Code generation failed from master: " + (body.getMessage() != null ? body.getMessage() : "No message")
                );
            }

            return body.getGeneratedCode();

        } catch (ResourceAccessException ex) {
            // timeout / connection refused / DNS
            throw ClaimException.internalError("Master service is unreachable while generating code", ex);
        } catch (HttpStatusCodeException ex) {
            // master returned 4xx/5xx
            // if master says 400, treat as bad request; otherwise internal
            if (ex.getStatusCode().is4xxClientError()) {
                throw ClaimException.badRequest(
                        "Master service rejected request for code generation. Status: " + ex.getStatusCode()
                );
            }
            throw ClaimException.internalError(
                    "Master service error while generating code. Status: " + ex.getStatusCode(),
                    ex
            );
        } catch (RestClientException ex) {
            throw ClaimException.internalError("Unexpected error while calling master service for code generation", ex);
        }
    }
    public String generateCode(String codeType, String prefix) {

        // Validate input early
        if (codeType == null || codeType.trim().isEmpty()) {
            throw ClaimException.badRequest("codeType is required");
        }
        if (prefix == null || prefix.trim().isEmpty()) {
            throw ClaimException.badRequest("prefix is required");
        }

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + generatePath)
                .queryParam("codeType", codeType)
                .queryParam("prefix", prefix)
                .toUriString();

        try {
            ResponseEntity<CodeGenerationResponseDto> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(defaultHeaders()),
                    CodeGenerationResponseDto.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw ClaimException.internalError("Master service code generation failed");
            }

            CodeGenerationResponseDto body = res.getBody();

            if (!body.isSuccess() || body.getGeneratedCode() == null || body.getGeneratedCode().isBlank()) {
                // this is a business failure from master, not a crash
                throw ClaimException.unprocessable(
                        "Code generation failed from master: " + (body.getMessage() != null ? body.getMessage() : "No message")
                );
            }

            return body.getGeneratedCode();

        } catch (ResourceAccessException ex) {
            // timeout / connection refused / DNS
            throw ClaimException.internalError("Master service is unreachable while generating code", ex);
        } catch (HttpStatusCodeException ex) {
    String body = ex.getResponseBodyAsString();

    if (ex.getStatusCode().is4xxClientError()) {
        throw ClaimException.badRequest(
                "Master service rejected request for code generation. Status: "
                        + ex.getStatusCode()
                        + ", Body: "
                        + body
        );
    }

    throw ClaimException.internalError(
            "Master service error while generating code. Status: "
                    + ex.getStatusCode()
                    + ", Body: "
                    + body,
            ex
    );
}
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
}
