package com.claim.claim_processing.client;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.claim.claim_processing.common.DTO.master.*;
import com.claim.claim_processing.exceptions.ClaimException;

@Component
@RequiredArgsConstructor
public class MasterDataClient {

    private final RestTemplate restTemplate;

    @Value("${app.master.base-url}")
    private String baseUrl;

    public PersonIdentityDTO getPersonIdentyById(Long identityTypeId) {
        String url = baseUrl + "/api/master-data/person-identity-credentials/" + identityTypeId;
        return getForObject(url, PersonIdentityDTO.class, "Person Identy Credentials", identityTypeId);
    }

    public CurrencyDTO getCurrencyById(Long id) {
        String url = baseUrl + "/api/master-data/currencies/get-by-id/" + id;
        return getForObject(url, CurrencyDTO.class, "Currency", id);
    }

    public AgencyTypeDTO getAgencyTypeById(Long id) {
        String url = baseUrl + "/api/master-data/agency-types/get-by-id/" + id;
        return getForObject(url, AgencyTypeDTO.class, "Agency Type", id);
    }

    public AgencyCategoryDTO getAgencyCategoryById(String id) {
        String url = baseUrl + "/api/master-data/agency-categories/get-by-id/" + id;
        return getByStringParam(
                url,
                AgencyCategoryDTO.class,
                "Agency Category Type",
                "id",
                id);
    }

    public BankTypeDTO getBankTypeById(Long id) {
        String url = baseUrl + "/api/master-data/bank-types/get-by-id/" + id;
        return getForObject(url, BankTypeDTO.class, "Bank Type", id);
    }

    public TitleOfCourtesyDTO getTitleById(Long id) {
        String url = baseUrl + "/api/master-data/titles-of-courtesy/get-by-id/" + id;
        return getForObject(url, TitleOfCourtesyDTO.class, "Title of Courtesy", id);
    }

    public StatusDTO getByApplicationStatus(Long id) {
        String url = baseUrl + "/api/master-data/status/by-id?id=" + id;
        return getForObject(url, StatusDTO.class, "Master Status", id);
    }

    public RelationTypeDTO getRelationType(Long id) {
        String url = baseUrl + "/api/master-data/relation-types/get-by-id/" + id;
        return getForObject(url, RelationTypeDTO.class, "Relation Types", id);
    }

    public MemberTypeDTO getMemberTypeById(Long id) {
        String url = baseUrl + "/api/master-data/member-types/" + id;
        return getForObject(url, MemberTypeDTO.class, "Member Type", id);
    }

    

    public EmploymentTypeDTO getEmploymentTypeById(Long id) {
        String url = baseUrl + "/api/master-data/employment-types/" + id;
        return getForObject(url, EmploymentTypeDTO.class, "Employment Type", id);
    }


    private <T> T getForObject(String url, Class<T> responseType, String entityName, Long id) {

        if (id == null || id <= 0) {
            throw ClaimException.badRequest(entityName + " ID is required and must be positive");
        }

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    responseType);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            // 2xx but no body OR non-2xx
            throw ClaimException
                    .internalError("Master service returned invalid response for " + entityName + " with ID: " + id);

        } catch (ResourceAccessException ex) {
            // timeouts, connection refused, DNS issues
            throw ClaimException
                    .internalError("Master data service is unreachable for " + entityName + " (ID: " + id + ")", ex);

        } catch (HttpStatusCodeException ex) {
            // 404 from master -> notFound; 4xx -> badRequest; 5xx -> internal
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ClaimException.notFound(entityName + " not found with ID: " + id);
            }
            if (ex.getStatusCode().is4xxClientError()) {
                throw ClaimException.badRequest("Master service rejected request for " + entityName + " (ID: "
                        + id + "). Status: " + ex.getStatusCode());
            }
            throw ClaimException.internalError(
                    "Master service error for " + entityName + " (ID: " + id + "). Status: " + ex.getStatusCode(), ex);

        } catch (RestClientException ex) {
            throw ClaimException.internalError(
                    "Unexpected error calling master service for " + entityName + " (ID: " + id + ")", ex);

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Unexpected error calling master service for " + entityName + " (ID: " + id + ")", ex);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "*/*");
        return headers;
    }

    private <T> T getByStringParam(String urlTemplate, Class<T> responseType, String entityName, String paramName,
            String paramValue) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "*/*");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    responseType,
                    paramValue // safely encoded by RestTemplate
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            throw new RuntimeException(entityName + " not found with " + paramName + ": " + paramValue);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error fetching " + entityName + " by " + paramName + ": " + e.getMessage(),
                    e);
        }
    }

}
