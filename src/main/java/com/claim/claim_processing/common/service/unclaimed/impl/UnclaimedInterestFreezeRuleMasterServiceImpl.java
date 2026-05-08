package com.claim.claim_processing.common.service.unclaimed.impl;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedInterestFreezeRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedInterestFreezeRuleResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedInterestFreezeRuleMaster;
import com.claim.claim_processing.common.mapper.unclaimed.InterestFreezeRuleMapper;
import com.claim.claim_processing.common.repository.unclaimed.InterestFreezeRuleRepository;
import com.claim.claim_processing.common.service.unclaimed.InterestFreezeRuleService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnclaimedInterestFreezeRuleMasterServiceImpl
        implements InterestFreezeRuleService {

    private final InterestFreezeRuleRepository repository;
    private final InterestFreezeRuleMapper mapper;

    // ================= CREATE =================
    @Override
    public UnclaimedInterestFreezeRuleResponseDto create(
            UnclaimedInterestFreezeRuleRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Interest freeze rule already exists with code: " + dto.getCode()
            );
        }

        UnclaimedInterestFreezeRuleMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // ================= UPDATE =================
    @Override
    public UnclaimedInterestFreezeRuleResponseDto update(
            Long id,
            UnclaimedInterestFreezeRuleRequestDto dto) {

        UnclaimedInterestFreezeRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Interest freeze rule not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Interest freeze rule already exists with code: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ================= GET BY ID =================
    @Override
    public UnclaimedInterestFreezeRuleResponseDto getById(Long id) {

        UnclaimedInterestFreezeRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Interest freeze rule not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public UnclaimedInterestFreezeRuleResponseDto getByCode(String code) {

        UnclaimedInterestFreezeRuleMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Interest freeze rule not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<UnclaimedInterestFreezeRuleResponseDto> getAll() {

        List<UnclaimedInterestFreezeRuleMaster> list = repository.findAll();

        list.sort(Comparator.comparing(UnclaimedInterestFreezeRuleMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<UnclaimedInterestFreezeRuleResponseDto> getAllActive() {

        List<UnclaimedInterestFreezeRuleMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(UnclaimedInterestFreezeRuleMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        UnclaimedInterestFreezeRuleMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Interest freeze rule not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}