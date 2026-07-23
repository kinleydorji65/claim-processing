package com.claim.claim_processing.application.service.application.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.service.application.ValidateComponent;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateComponentImpl implements ValidateComponent {
    private final ReserveAccountRepository reserveAccountRepository;

    @Override
    public ApiResponseDTO<String> validateComponent(String nppfNumber, String componentCode) {
        Boolean exist = reserveAccountRepository.existsByNppfNumberAndComponentCodeAndIsActive(nppfNumber,
                componentCode, "Y");
        if (exist) {
            return ApiResponseDTO.conflict("Already Claimed.");
        }
        return ApiResponseDTO.success("It is Claimable.");
    }
}
