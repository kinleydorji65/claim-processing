package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.mapper.claim.ClaimEligibilityMapper;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimEligibilityServiceImpl implements ClaimEligibilityService {

    private final ClaimEligibilityRepository claimEligibilityRepository;
    private final ClaimEligibilityMapper claimEligibilityMapper;

    private final ClaimCircumstanceRepository claimCircumstanceRepository;
    private final CessationTypeRepository cessationTypeRepository;
    private final SchemeTypeRepository schemeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClaimEligibilityResponseDto> getAllActive() {
        List<ClaimEligibilityMaster> entities =
                claimEligibilityRepository.findByIsActiveOrderByRuleNameAsc(ActivityEnum.Y);

        return claimEligibilityMapper.toResponseDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimEligibilityResponseDto getById(Long id) {
        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));

        return claimEligibilityMapper.toResponseDto(entity);
    }

    @Override
    public ClaimEligibilityResponseDto create(ClaimEligibilityCreateRequestDto requestDto) {

        if (claimEligibilityRepository.existsByRuleCode(requestDto.getRuleCode())) {
            throw new RuntimeException("Claim eligibility rule code already exists: " + requestDto.getRuleCode());
        }

        ClaimEligibilityMaster entity = claimEligibilityMapper.toEntity(requestDto);

        if (requestDto.getClaimCircumstanceId() != null) {
            ClaimCircumstanceMaster claimCircumstance = claimCircumstanceRepository.findById(requestDto.getClaimCircumstanceId())
                    .orElseThrow(() -> new RuntimeException("Claim circumstance not found with id: " + requestDto.getClaimCircumstanceId()));
            entity.setClaimCircumstance(claimCircumstance);
        }

        if (requestDto.getCessationTypeId() != null) {
            CessationTypeMaster cessationType = cessationTypeRepository.findById(requestDto.getCessationTypeId())
                    .orElseThrow(() -> new RuntimeException("Cessation type not found with id: " + requestDto.getCessationTypeId()));
            entity.setCessationType(cessationType);
        }

        if (requestDto.getSchemeTypeId() != null) {
            SchemeMaster schemeType = schemeRepository.findById(requestDto.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException("Scheme type not found with id: " + requestDto.getSchemeTypeId()));
            entity.setSchemeType(schemeType);
        }

        entity.setCreatedBy("SYSTEM");

        ClaimEligibilityMaster saved = claimEligibilityRepository.save(entity);
        return claimEligibilityMapper.toResponseDto(saved);
    }

    @Override
    public ClaimEligibilityResponseDto update(Long id, ClaimEligibilityUpdateRequestDto requestDto) {

        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));

        claimEligibilityMapper.updateEntityFromDto(requestDto, entity);

        if (requestDto.getClaimCircumstanceId() != null) {
            ClaimCircumstanceMaster claimCircumstance = claimCircumstanceRepository.findById(requestDto.getClaimCircumstanceId())
                    .orElseThrow(() -> new RuntimeException("Claim circumstance not found with id: " + requestDto.getClaimCircumstanceId()));
            entity.setClaimCircumstance(claimCircumstance);
        }

        if (requestDto.getCessationTypeId() != null) {
            CessationTypeMaster cessationType = cessationTypeRepository.findById(requestDto.getCessationTypeId())
                    .orElseThrow(() -> new RuntimeException("Cessation type not found with id: " + requestDto.getCessationTypeId()));
            entity.setCessationType(cessationType);
        }

        if (requestDto.getSchemeTypeId() != null) {
            SchemeMaster schemeType = schemeRepository.findById(requestDto.getSchemeTypeId())
                    .orElseThrow(() -> new RuntimeException("Scheme type not found with id: " + requestDto.getSchemeTypeId()));
            entity.setSchemeType(schemeType);
        }

        entity.setUpdatedBy("SYSTEM");

        ClaimEligibilityMaster updated = claimEligibilityRepository.save(entity);
        return claimEligibilityMapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {
        ClaimEligibilityMaster entity = claimEligibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim eligibility rule not found with id: " + id));

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        claimEligibilityRepository.save(entity);
    }
}