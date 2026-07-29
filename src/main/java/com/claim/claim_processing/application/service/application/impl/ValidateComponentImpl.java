package com.claim.claim_processing.application.service.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.service.application.ValidateComponentService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.integration.pension.entity.PensionContributionComponent;
import com.claim.claim_processing.integration.pension.repository.PensionContributionComponentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateComponentImpl implements ValidateComponentService {
    private final ReserveAccountRepository reserveAccountRepository;
    private final PensionContributionComponentRepository pensionContributionComponentRepository;

    @Override
    public ApiResponseDTO<String> validateComponent(String nppfNumber, String componentCode) {
        Boolean exist = reserveAccountRepository.existsByNppfNumberAndComponentCodeAndIsActive(nppfNumber,
                componentCode, "Y");
        if (!exist) {
            return ApiResponseDTO.success("It is Claimable.");
        }
        Boolean pensionExisted = pensionContributionComponentRepository.existsActiveComponentByNppfAndComponentCode(nppfNumber, componentCode);
        if (!pensionExisted) {
            return ApiResponseDTO.success("It is Claimable.");
        }
        // PensionApplication pension = pensionApplicationRepository.findById(long 1l);
        return ApiResponseDTO.conflict("Already Claimed.");
    }

    @Override
    public ApiResponseDTO<String> updateComponents(String nppfNumber, List<SpecialCaseComponentBalanceResponseDTO> components) {
        
        components.
            stream()
            .map(m -> {
                ReserveAccount entity = reserveAccountRepository.findByNppfNumberAndComponentCodeAndIsActive(nppfNumber, m.getCode(), "A").orElse(null);
                
        
                if (entity != null) {
                    entity.setIsActive("N");
                    reserveAccountRepository.saveAndFlush(entity);
                }
                PensionContributionComponent pensionComponent = pensionContributionComponentRepository.findActiveComponentsByNppfAndComponentCode(nppfNumber, m.getCode()).orElse(null);
                if (pensionComponent != null) {
                    pensionComponent.setIsActive("N");
                    pensionContributionComponentRepository.saveAndFlush(pensionComponent);
                }

                return entity;
            })
            .toString();

        return null;
    }
}
