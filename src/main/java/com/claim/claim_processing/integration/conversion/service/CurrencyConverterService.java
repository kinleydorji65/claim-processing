package com.claim.claim_processing.integration.conversion.service;

import java.util.List;
import java.util.Set;

import com.claim.claim_processing.integration.conversion.dto.*;

public interface CurrencyConverterService {
    CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request);
}
