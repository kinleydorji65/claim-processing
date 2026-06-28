package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationForfeitedComponentPatchRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;
import com.claim.claim_processing.application.repository.application.ClaimApplicationForfeitedComponentRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationForfeitedComponentService;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimApplicationForfeitedComponentServiceImpl
        implements ClaimApplicationForfeitedComponentService {

    private final ClaimApplicationForfeitedComponentRepository forfeitedComponentRepository;

    @Override
    @Transactional
    public List<ClaimApplicationForfeitedComponent> saveForfeitedComponents(
            ClaimApplication claimApplication,
            ClaimCalculationResponseDTO calculationResponse,
            String createdBy) {

        if (claimApplication == null || calculationResponse == null) {
            return null;
        }

        if (calculationResponse.getForfeitedComponents() == null
                || calculationResponse.getForfeitedComponents().isEmpty()) {
            return null;
        }
        List<ClaimApplicationForfeitedComponent> savedComponents = new ArrayList<>();
        for (ClaimCalculationResponseDTO.ComponentBalanceDTO component : calculationResponse.getForfeitedComponents()) {

            if (component == null || component.getCode() == null) {
                continue;
            }

            boolean exists = forfeitedComponentRepository
                    .existsByClaimApplication_IdAndComponentCodeAndIsActive(
                            claimApplication.getId(),
                            component.getCode(),
                            ActivityEnum.Y);

            if (exists) {
                continue;
            }

            ClaimApplicationForfeitedComponent entity = ClaimApplicationForfeitedComponent.builder()
                    .claimApplication(claimApplication)
                    .componentCode(component.getCode())
                    .componentName(component.getName())
                    .componentType(component.getType())
                    .amount(component.getAmount())
                    .reason("Forfeited from lapsed rule calculation")
                    .subClaimCode(component.getSubRuleCode())
                    .createdBy(createdBy)
                    .isActive(ActivityEnum.Y)
                    .build();

            forfeitedComponentRepository.save(entity);
            savedComponents.add(entity);
        }
        return savedComponents;
    }

    @Override
    @Transactional
    public List<ClaimApplicationForfeitedComponent> patchForfeitedComponent(
            List<ClaimApplicationForfeitedComponentPatchRequestDto> requests) {

        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<ClaimApplicationForfeitedComponent> updatedComponents = new ArrayList<>();

        for (ClaimApplicationForfeitedComponentPatchRequestDto request : requests) {

            if (request == null || request.getForfeitedComponentId() == null) {
                continue;
            }

            ClaimApplicationForfeitedComponent component = forfeitedComponentRepository
                    .findById(request.getForfeitedComponentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Forfeited component not found with id: "
                                    + request.getForfeitedComponentId()));

            if (request.getAmount() != null) {
                component.setAmount(request.getAmount());
            }

            if (request.getReason() != null) {
                component.setReason(request.getReason());
            }

            if (request.getIsActive() != null) {
                component.setIsActive(request.getIsActive());
            }

            if (request.getUpdatedBy() != null) {
                component.setUpdatedBy(request.getUpdatedBy());
            }

            updatedComponents.add(component);
        }

        return forfeitedComponentRepository.saveAll(updatedComponents);
    }
}