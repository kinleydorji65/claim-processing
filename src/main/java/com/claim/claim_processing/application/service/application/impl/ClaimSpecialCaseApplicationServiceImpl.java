package com.claim.claim_processing.application.service.application.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
import com.claim.claim_processing.application.mapper.application.ClaimSpecialCaseApplicationMapper;
import com.claim.claim_processing.application.repository.application.ClaimSpecialCaseApplicationRepository;
import com.claim.claim_processing.application.service.application.ClaimSpecialCaseApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.service.claim.impl.ReserveAccountServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimSpecialCaseApplicationServiceImpl implements ClaimSpecialCaseApplicationService {
    private final ClaimSpecialCaseApplicationMapper claimSpecialCaseApplicationMapper;
    private final ReserveAccountServiceImpl reserveAccountService;
    private final ClaimSpecialCaseApplicationRepository claimSpecialCaseApplicationRepository;

    public ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> create(ClaimSpecialCaseApplicationRequestDto dto, ClaimApplication claimApplication) {
        try {
            
            // Validate reserve account if provided
            if (dto.getReserveAccountId() != null) {
                reserveAccountService.getById(dto.getReserveAccountId());
            }
            
            // Create and set default values
            ClaimSpecialCaseApplication entity = claimSpecialCaseApplicationMapper.toEntity(dto);
            entity.setRequestDate(LocalDateTime.now());
            entity.setIsActive("Y");
            entity.setCreatedBy(getCurrentUser());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setClaimApplication(claimApplication);
            
            // Set reserve account from claim if not provided
            if (dto.getReserveAccountId() == null && claimApplication != null) {
                Long reserveId = claimApplication.getClaimSpecialCaseApplication().getReserveAccountId();
                if (reserveId != null) {
                    entity.setReserveAccountId(reserveId);
                }
            }
            
            // Save and return
            ClaimSpecialCaseApplication saved = claimSpecialCaseApplicationRepository.saveAndFlush(entity);
            ClaimSpecialCaseApplicationResponseDto response = claimSpecialCaseApplicationMapper.toResponseDto(saved);
            response.setClaimApplicationId(claimApplication != null ? claimApplication.getId() : null);
            
            return ApiResponseDTO.success(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    
    private String getCurrentUser() {
        // Implementation to get current user
        return "SYSTEM";
    }
}
