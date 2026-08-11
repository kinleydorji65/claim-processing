package com.claim.claim_processing.integration.conversion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for currency conversion operations
 * Contains the converted amount, exchange rate, and metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyConversionResponse {
    private String nppfNumber;
    /**
     * Source currency code (e.g., "USD")
     */
    private String sourceCurrency;
    
    /**
     * Target currency code (e.g., "EUR")
     */
    private String targetCurrency;
    
    /**
     * Original amount in source currency
     */
    private BigDecimal sourceAmount;
    
    /**
     * Converted amount in target currency
     */
    private BigDecimal convertedAmount;
    
    /**
     * Exchange rate used for conversion (1 source currency = X target currency)
     */
    private BigDecimal exchangeRate;
    
    /**
     * Timestamp when the conversion was performed
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime conversionTimestamp;
}
