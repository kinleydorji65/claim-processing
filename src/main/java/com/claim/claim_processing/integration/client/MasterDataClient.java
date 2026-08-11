package com.claim.claim_processing.integration.client;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.CountryDTO;
import com.claim.claim_processing.integration.contribution.dto.DzongkhagDTO;
import com.claim.claim_processing.integration.contribution.dto.GewogDTO;
import com.claim.claim_processing.integration.contribution.dto.NationalityDTO;
import com.claim.claim_processing.integration.contribution.dto.VillageDTO;

@Component
@RequiredArgsConstructor
public class MasterDataClient {

    private final RestTemplate restTemplate;

    @Value("${app.master.base-url}")
    private String baseUrl;

    public DzongkhagDTO getDzongkhagById(Long id) {
        String url = baseUrl + "/api/master-data/dzongkhags/" + id;
        return getForObject(url, DzongkhagDTO.class, "Dzongkhag", id);
    }

    public GewogDTO getGewogById(Long id) {
        String url = baseUrl + "/api/master-data/gewogs/" + id;
        return getForObject(url, GewogDTO.class, "Gewog", id);
    }

    public VillageDTO getVillageById(Long id) {
        String url = baseUrl + "/api/master-data/villages/" + id;
        return getForObject(url, VillageDTO.class, "Village", id);
    }

    public CountryDTO getCountryById(Long id) {
        String url = baseUrl + "/api/master-data/countries/get-by-id/" + id;
        return getForObject(url, CountryDTO.class, "Country", id);
    }

    public NationalityDTO getNationalityById(Long id) {
        String url = baseUrl + "/api/master-data/nationalities/get-by-id/" + id;
        return getForObject(url, NationalityDTO.class, "Nationality", id);
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

}

