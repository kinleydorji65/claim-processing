package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ClaimLapsedRefundMapper;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimLapsedRefundServiceImpl implements ClaimLapsedRefundService {

    private final ClaimLapsedRefundRepository repository;
    private final ClaimLapsedRefundMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public ClaimLapsedRefundResponseDto create(ClaimLapsedRefundRequestDto dto) {

        ClaimLapsedRefundMaster entity = mapper.toEntity(dto);
        ClaimLapsedRefundMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public ClaimLapsedRefundResponseDto getById(Long id) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    public List<ClaimLapsedRefundResponseDto> getAll() {

        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public ClaimLapsedRefundResponseDto update(Long id, ClaimLapsedRefundRequestDto dto) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));

        mapper.updateEntityFromDto(dto, entity);

        ClaimLapsedRefundMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    public void delete(Long id) {

        ClaimLapsedRefundMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Lapsed Refund not found"));

        repository.delete(entity);
    }

    // -------------------------------
    // ACTIVE RULES
    // -------------------------------
    @Override
    public List<ClaimLapsedRefundResponseDto> getActiveRules() {

        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // -------------------------------
    // VALID RULES BY DATE (RULE ENGINE CORE)
    // -------------------------------
    @Override
    public List<ClaimLapsedRefundResponseDto> getValidRulesByDate(LocalDate date) {

        return mapper.toResponseDtoList(
                repository.findByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                        date, date
                )
        );
    }

    // -------------------------------
    // GET BY RULE CODE
    // -------------------------------
    @Override
    public ClaimLapsedRefundResponseDto getByRuleCode(String ruleCode) {

        ClaimLapsedRefundMaster entity = repository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // FK FILTER METHODS
    // -------------------------------
    @Override
    public List<ClaimLapsedRefundResponseDto> getByClaimCircumstance(Long claimCircumstanceId) {

        return mapper.toResponseDtoList(
                repository.findByClaimCircumstance_Id(claimCircumstanceId)
        );
    }

    @Override
    public List<ClaimLapsedRefundResponseDto> getByCessationType(Long cessationTypeId) {

        return mapper.toResponseDtoList(
                repository.findByCessationType_Id(cessationTypeId)
        );
    }

    @Override
    public List<ClaimLapsedRefundResponseDto> getBySchemeType(Long schemeTypeId) {

        return mapper.toResponseDtoList(
                repository.findBySchemeType_Id(schemeTypeId)
        );
    }
}