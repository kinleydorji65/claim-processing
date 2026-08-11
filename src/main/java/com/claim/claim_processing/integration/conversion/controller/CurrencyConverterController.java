package com.claim.claim_processing.integration.conversion.controller;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.conversion.dto.CurrencyConversionRequest;
import com.claim.claim_processing.integration.conversion.dto.CurrencyConversionResponse;
import com.claim.claim_processing.integration.conversion.service.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterController {

    private final CurrencyConverterService currencyConversionService;

    /**
     * Convert member's total contribution balance to target currency
     * POST /api/v1/currency/convert
     */
    @PostMapping("/convert")
    public ResponseEntity<ApiResponseDTO<CurrencyConversionResponse>> convertCurrency(
            @RequestBody CurrencyConversionRequest request) {

        log.info("Received currency conversion request for NPPF: {} to {}", 
                request.getNppfNumber(), request.getTargetCurrency());

        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Currency converted successfully", response)
        );
    }
}