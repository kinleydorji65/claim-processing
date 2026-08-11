package com.claim.claim_processing.integration.conversion.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse;
import com.claim.claim_processing.integration.contribution.service.FullContributionHistoryService;
import com.claim.claim_processing.integration.conversion.dto.CurrencyConversionRequest;
import com.claim.claim_processing.integration.conversion.dto.CurrencyConversionResponse;
import com.claim.claim_processing.integration.conversion.service.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    private final FullContributionHistoryService fullContributionHistoryService;
    private final RestTemplate restTemplate;

    private static final String DEFAULT_SOURCE_CURRENCY = "BTN";
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    // ExchangeRate-API (free, supports BTN)
    @Value("${app.currency-converter.currency-url}")
    private String EXCHANGE_API_URL;

    @Override
    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) {
        log.info("Converting currency for NPPF: {} to {}", request.getNppfNumber(), request.getTargetCurrency());

        try {
            // Validate
            if (request.getTargetCurrency() == null || request.getTargetCurrency().trim().isEmpty()) {
                throw ClaimException.badRequest("Target currency is required");
            }

            // Get contribution history
            ApiResponseDTO<FullContributionHistoryResponse> response = 
                    fullContributionHistoryService.getFullContributionHistory(request.getNppfNumber());

            if (response == null || response.getData() == null) {
                throw ClaimException.notFound("No contribution history found for member: " + request.getNppfNumber());
            }

            FullContributionHistoryResponse historyData = response.getData();

            // Calculate total amount
            BigDecimal totalAmount = historyData.getTotalBalance() != null ? historyData.getTotalBalance() : BigDecimal.ZERO;
            
            // Add excess if exists
            if (historyData.getExcessService() != null && 
                historyData.getExcessService().getIsEligible() != null &&
                historyData.getExcessService().getIsEligible()) {
                BigDecimal excess = historyData.getExcessService().getTotalExcessAmount() != null 
                        ? historyData.getExcessService().getTotalExcessAmount() 
                        : BigDecimal.ZERO;
                totalAmount = totalAmount.add(excess);
            }

            log.info("Total amount for NPPF {}: {} BTN", request.getNppfNumber(), totalAmount);

            // Convert currency
            String sourceCurrencyCode = DEFAULT_SOURCE_CURRENCY;
            String targetCurrencyCode = request.getTargetCurrency().toUpperCase().trim();

            // If same currency
            if (sourceCurrencyCode.equals(targetCurrencyCode)) {
                return CurrencyConversionResponse.builder()
                        .nppfNumber(request.getNppfNumber())
                        .sourceCurrency(sourceCurrencyCode)
                        .targetCurrency(targetCurrencyCode)
                        .sourceAmount(totalAmount.setScale(SCALE, ROUNDING_MODE))
                        .convertedAmount(totalAmount.setScale(SCALE, ROUNDING_MODE))
                        .exchangeRate(BigDecimal.ONE)
                        .build();
            }

            // Get exchange rate from API
            BigDecimal exchangeRate = getExchangeRate(sourceCurrencyCode, targetCurrencyCode);
            
            // Calculate converted amount
            BigDecimal convertedAmount = totalAmount.multiply(exchangeRate);

            log.info("Conversion: {} {} = {} {} (Rate: {})", 
                    totalAmount, sourceCurrencyCode, convertedAmount, targetCurrencyCode, exchangeRate);

            // Build response
            return CurrencyConversionResponse.builder()
                    .nppfNumber(request.getNppfNumber())
                    .sourceCurrency(sourceCurrencyCode)
                    .targetCurrency(targetCurrencyCode)
                    .sourceAmount(totalAmount.setScale(SCALE, ROUNDING_MODE))
                    .convertedAmount(convertedAmount.setScale(SCALE, ROUNDING_MODE))
                    .exchangeRate(exchangeRate.setScale(4, ROUNDING_MODE))
                    .build();

        } catch (Exception e) {
            log.error("Currency conversion failed: {}", e.getMessage(), e);
            throw ClaimException.internalError("Currency conversion failed: " + e.getMessage());
        }
    }

    private BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency) {
        try {
            String url = EXCHANGE_API_URL + sourceCurrency;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("rates")) {
                throw new RuntimeException("Failed to get exchange rates");
            }
            
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            
            if (!rates.containsKey(targetCurrency)) {
                throw new RuntimeException("Currency " + targetCurrency + " not supported");
            }
            
            return BigDecimal.valueOf(rates.get(targetCurrency));
            
        } catch (Exception e) {
            log.error("Failed to get exchange rate from API: {}", e.getMessage());
            throw new RuntimeException("Failed to get exchange rate for " + targetCurrency);
        }
    }
}