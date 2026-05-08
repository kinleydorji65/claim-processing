package com.claim.claim_processing.common.service.unclaimed.impl;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedPeriodRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedPeriodRuleResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedPeriodRuleMaster;
import com.claim.claim_processing.common.mapper.unclaimed.UnclaimedPeriodRuleMasterMapper;
import com.claim.claim_processing.common.repository.unclaimed.UnclaimedPeriodRuleMasterRepository;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedPeriodRuleMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnclaimedPeriodRuleMasterServiceImpl
        implements UnclaimedPeriodRuleMasterService {

    private final UnclaimedPeriodRuleMasterRepository repository;
    private final UnclaimedPeriodRuleMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public UnclaimedPeriodRuleResponseDto create(
            UnclaimedPeriodRuleRequestDto dto
    ) {

        if (repository.existsByRuleName(dto.getRuleName())) {
            throw ClaimException.conflict(
                    "Unclaimed period rule already exists: " + dto.getRuleName()
            );
        }

        UnclaimedPeriodRuleMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        UnclaimedPeriodRuleMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public UnclaimedPeriodRuleResponseDto update(
            Long id,
            UnclaimedPeriodRuleRequestDto dto
    ) {

        UnclaimedPeriodRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed period rule not found with id: " + id
                        )
                );

        if (dto.getRuleName() != null &&
                !dto.getRuleName().equals(entity.getRuleName()) &&
                repository.existsByRuleName(dto.getRuleName())) {

            throw ClaimException.conflict(
                    "Unclaimed period rule already exists: " + dto.getRuleName()
            );
        }

        mapper.updateEntity(entity, dto);

        UnclaimedPeriodRuleMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public UnclaimedPeriodRuleResponseDto getById(Long id) {

        UnclaimedPeriodRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed period rule not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY RULE NAME =================
    @Override
    public UnclaimedPeriodRuleResponseDto getByRuleName(String ruleName) {

        UnclaimedPeriodRuleMaster entity = repository.findByRuleName(ruleName)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed period rule not found: " + ruleName
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<UnclaimedPeriodRuleResponseDto> getAll() {

        List<UnclaimedPeriodRuleMaster> list = repository.findAll();

        list.sort(
                Comparator.comparing(
                        UnclaimedPeriodRuleMaster::getRuleName
                )
        );

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<UnclaimedPeriodRuleResponseDto> getAllActive() {

        List<UnclaimedPeriodRuleMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(
                Comparator.comparing(
                        UnclaimedPeriodRuleMaster::getRuleName
                )
        );

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        UnclaimedPeriodRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed period rule not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }

    // ================= GET BY PERIOD VALUE =================
    @Override
    public UnclaimedPeriodRuleResponseDto getByPeriodValue(Integer periodValue) {

        UnclaimedPeriodRuleMaster entity = repository.findByPeriodValue(periodValue)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed period rule not found with period value: " + periodValue
                        )
                );

        return mapper.toResponseDto(entity);
    }
}