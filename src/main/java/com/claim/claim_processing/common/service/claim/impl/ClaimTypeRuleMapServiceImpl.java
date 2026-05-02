package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeRuleMapRequestDto;
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
    public ClaimTypeRuleMapResponseDto create(ClaimTypeRuleMapRequestDto requestDto) {

        validateRequest(requestDto);

        validateDuplicate(requestDto.getClaimTypeId(), requestDto.getRuleId());

        ClaimTypeRuleMap entity = new ClaimTypeRuleMap();
        entity.setClaimType(getClaimType(requestDto.getClaimTypeId()));
        entity.setRuleType(getRuleType(requestDto.getRuleId()));

        return mapper.toResponseDto(repository.save(entity));
    }

    /* ========================= UPDATE ========================= */

    @Override
    public ClaimTypeRuleMapResponseDto update(Long id, ClaimTypeRuleMapRequestDto requestDto) {

        validateRequest(requestDto);

        ClaimTypeRuleMap existing = getEntity(id);

        boolean isDuplicate = repository.existsByClaimTypeIdAndRuleTypeId(
                requestDto.getClaimTypeId(),
                requestDto.getRuleId()
        );

        boolean isSameRecord =
                existing.getClaimType().getId().equals(requestDto.getClaimTypeId()) &&
                        existing.getRuleType().getId().equals(requestDto.getRuleId());

        if (isDuplicate && !isSameRecord) {
            throw ClaimException.conflict("Mapping already exists");
        }

        existing.setClaimType(getClaimType(requestDto.getClaimTypeId()));
        existing.setRuleType(getRuleType(requestDto.getRuleId()));

        return mapper.toResponseDto(repository.save(existing));
    }

    /* ========================= READ ========================= */

    @Override
    @Transactional(readOnly = true)
    public ClaimTypeRuleMapResponseDto getById(Long id) {
        return mapper.toResponseDto(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimTypeRuleMapResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
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
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    /* ========================= PRIVATE HELPERS ========================= */

    private ClaimTypeRuleMap getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Claim Type Rule Map", String.valueOf(id))
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

    private void validateRequest(ClaimTypeRuleMapRequestDto dto) {

        if (dto == null) {
            throw ClaimException.badRequest("Request cannot be null");
        }

        if (dto.getClaimTypeId() == null) {
            throw ClaimException.singleValidationError("claimTypeId", "Required field");
        }

        if (dto.getRuleId() == null) {
            throw ClaimException.singleValidationError("ruleId", "Required field");
        }
    }
}