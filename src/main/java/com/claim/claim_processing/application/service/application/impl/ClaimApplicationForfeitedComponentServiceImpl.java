package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationForfeitedComponentRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;
import com.claim.claim_processing.application.repository.application.ClaimApplicationForfeitedComponentRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationForfeitedComponentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimApplicationForfeitedComponentServiceImpl
        implements ClaimApplicationForfeitedComponentService {

    private final ClaimApplicationForfeitedComponentRepository forfeitedComponentRepository;

    @Override
    @Transactional
    public List<ClaimApplicationForfeitedComponent> saveForfeitedComponents(
            ClaimApplication claimApplication,
            List<ClaimApplicationForfeitedComponentRequestDto> forfeitedComponents) {

        if (claimApplication == null) {
            log.error("Claim application is null");
            return Collections.emptyList();
        }

        if (forfeitedComponents == null || forfeitedComponents.isEmpty()) {
            log.info("No forfeited components to save for claim: {}", 
                    claimApplication.getApplicationNumber());
            return Collections.emptyList();
        }

        log.info("Saving {} forfeited components for claim: {}", 
                forfeitedComponents.size(), claimApplication.getApplicationNumber());

        List<ClaimApplicationForfeitedComponent> savedComponents = new ArrayList<>();

        for (ClaimApplicationForfeitedComponentRequestDto component : forfeitedComponents) {
            
            if (component == null || component.getComponentCode() == null) {
                log.warn("Skipping null component or component with null code");
                continue;
            }
            // ✅ FIX: Properly handle findById with orElse
            ClaimApplicationForfeitedComponent entity = null;
            
            if (component.getForfeitedComponentId() != null && component.getForfeitedComponentId() > 0) {
                entity = forfeitedComponentRepository
                        .findById(component.getForfeitedComponentId())
                        .orElse(null);
            }

            if (entity == null) {
                // Create new entity
                entity = ClaimApplicationForfeitedComponent.builder()
                        .claimApplication(claimApplication)
                        .componentCode(component.getComponentCode())
                        .componentName(component.getComponentName())
                        .componentType(component.getComponentType())
                        .amount(component.getAmount())
                        .reason(component.getReason() != null ? 
                                component.getReason() : "Forfeited from lapsed rule calculation")
                        .subClaimCode(component.getSubClaimCode())
                        .createdBy(component.getCreatedBy())
                        .build();
            } else {
                // Update existing entity
                entity.setClaimApplication(claimApplication);
                entity.setComponentCode(component.getComponentCode());
                entity.setComponentName(component.getComponentName());
                entity.setComponentType(component.getComponentType());
                entity.setAmount(component.getAmount());
                if (component.getReason() != null) {
                    entity.setReason(component.getReason());
                }
                entity.setSubClaimCode(component.getSubClaimCode());
                entity.setUpdatedBy(component.getUpdatedBy());
            }

            ClaimApplicationForfeitedComponent saved = forfeitedComponentRepository.saveAndFlush(entity);
            savedComponents.add(saved);
            log.info("Saved forfeited component: {} with amount: {}", 
                    component.getComponentCode(), component.getAmount());
        }

        log.info("Successfully saved {} forfeited components for claim: {}", 
                savedComponents.size(), claimApplication.getApplicationNumber());
        return savedComponents;
    }

    @Override
    @Transactional
    public List<ClaimApplicationForfeitedComponent> patchForfeitedComponent(
            List<ClaimApplicationForfeitedComponentRequestDto> requests) {

        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<ClaimApplicationForfeitedComponent> updatedComponents = new ArrayList<>();

        for (ClaimApplicationForfeitedComponentRequestDto request : requests) {

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

            if (request.getUpdatedBy() != null) {
                component.setUpdatedBy(request.getUpdatedBy());
            }

            updatedComponents.add(component);
        }

        return forfeitedComponentRepository.saveAll(updatedComponents);
    }
}