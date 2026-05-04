package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalAccumulationMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalAccumulationRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalAccumulationService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalAccumulationServiceImpl implements PartialWithdrawalAccumulationService {

    private final PartialWithdrawalAccumulationRepository repository;
    private final PartialWithdrawalAccumulationMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public PartialWithdrawalAccumulationResponseDto create(
            PartialWithdrawalAccumulationRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Accumulation code already exists: " + dto.getCode());
        }

        PartialWithdrawalAccumulationMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toDto(repository.save(entity));
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public PartialWithdrawalAccumulationResponseDto update(
            Long id,
            PartialWithdrawalAccumulationRequestDto dto) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Accumulation", String.valueOf(id))
                );

        mapper.updateEntity(entity, dto);

        return mapper.toDto(repository.save(entity));
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public PartialWithdrawalAccumulationResponseDto getById(Long id) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Accumulation", String.valueOf(id))
                );

        if (entity.getIsActive() == ActivityEnum.N) {
            throw ClaimException.badRequest("Accumulation is inactive");
        }

        return mapper.toDto(entity);
    }

    // =========================
    // GET ALL (INCLUDING INACTIVE)
    // =========================
    @Override
    public List<PartialWithdrawalAccumulationResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================
    // GET ALL ACTIVE ONLY
    // =========================
    @Override
    public List<PartialWithdrawalAccumulationResponseDto> getAllActive() {

        return repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================
    // DELETE (SOFT DELETE)
    // =========================
    @Override
    public void delete(Long id) {

        PartialWithdrawalAccumulationMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Accumulation", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}