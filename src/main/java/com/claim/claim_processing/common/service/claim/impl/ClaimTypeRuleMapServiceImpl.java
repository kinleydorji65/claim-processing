package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.mapper.claim.ClaimTypeRuleMapMapper;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.claim.ClaimTypeRuleMapService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimTypeRuleMapServiceImpl implements ClaimTypeRuleMapService {

    private final ClaimTypeRuleMapRepository repository;
    private final ClaimTypeMasterRepository claimTypeRepository;
    private final RuleTypeRepository ruleTypeRepository;
    private final ClaimTypeRuleMapMapper mapper;

    /* ========================= CREATE ========================= */

    @Override
    public ClaimTypeRuleMapResponseDto create(List<Long> ruleIds, Long claimTypeId) {
        ClaimTypeRuleMap entity = new ClaimTypeRuleMap();
        for (Long ruleId : ruleIds) {
            validateDuplicate(claimTypeId, ruleId);
            validateRequest(ruleId, claimTypeId);
            
            entity.setClaimType(getClaimType(claimTypeId));
            entity.setRuleType(getRuleType(ruleId));
            repository.save(entity);
        }
        return mapper.toResponseDto(entity);
    }

    /* ========================= UPDATE ========================= */

    @Override
    public ClaimTypeRuleMapResponseDto update(List<Long> ruleIds, Long claimTypeId) {
        ClaimTypeRuleMap existing = null;
                for (Long ruleId : ruleIds) {
                    validateRequest(ruleId, claimTypeId);

        existing = getEntity(ruleId, claimTypeId);

            boolean isDuplicate = repository.existsByClaimTypeIdAndRuleTypeId(
                claimTypeId,
                ruleId
        );

        boolean isSameRecord =
                existing.getClaimType().getId().equals(claimTypeId) &&
                        existing.getRuleType().getId().equals(ruleId);

        if (isDuplicate && !isSameRecord) {
            throw ClaimException.conflict("Mapping already exists");
        }

        existing.setClaimType(getClaimType(claimTypeId));
        existing.setRuleType(getRuleType(ruleId));
        }
        

        return mapper.toResponseDto(repository.save(existing));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ClaimTypeRuleMapResponseDto> getByClaimTypeId(Long claimTypeId) {

        validateClaimTypeExists(claimTypeId);

        return repository.findByClaimTypeId(claimTypeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimTypeRuleMapResponseDto> getByRuleTypeId(Long ruleTypeId) {

        validateRuleTypeExists(ruleTypeId);

        return repository.findByRuleTypeId(ruleTypeId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    /* ========================= DELETE ========================= */

    @Override
    public void delete(Long ruleId, Long claimTypeId) {
        repository.delete(getEntity(ruleId, claimTypeId));
    }

    /* ========================= PRIVATE HELPERS ========================= */

    private ClaimTypeRuleMap getEntity(Long ruleId, Long claimTypeId) {
        return repository.findByClaimType_IdAndRuleType_Id(claimTypeId, ruleId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Type Rule Map", "Claim Type ID: " + claimTypeId + ", Rule ID: " + ruleId)
                );
    }

    private ClaimTypeMaster getClaimType(Long id) {
        return claimTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Type", String.valueOf(id))
                );
    }

    private RuleTypeMaster getRuleType(Long id) {
        return ruleTypeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Rule Type", String.valueOf(id))
                );
    }

    private void validateClaimTypeExists(Long id) {
        if (!claimTypeRepository.existsById(id)) {
            throw ClaimException.resourceNotFound("Claim Type", String.valueOf(id));
        }
    }

    private void validateRuleTypeExists(Long id) {
        if (!ruleTypeRepository.existsById(id)) {
            throw ClaimException.resourceNotFound("Rule Type", String.valueOf(id));
        }
    }

    private void validateDuplicate(Long claimTypeId, Long ruleId) {
        if (repository.existsByClaimTypeIdAndRuleTypeId(claimTypeId, ruleId)) {
            throw ClaimException.conflict("Mapping already exists");
        }
    }

    private void validateRequest(Long ruleId, Long claimTypeId) {

        if (ruleId == null) {
            throw ClaimException.badRequest("Rule ID cannot be null");
        }

        if (claimTypeId == null) {
            throw ClaimException.badRequest("Claim Type ID cannot be null");
        }
    
            throw ClaimException.singleValidationError("ruleId", "Required field");
        
    }
}