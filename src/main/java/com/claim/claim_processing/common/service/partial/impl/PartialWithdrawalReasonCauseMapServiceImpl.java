package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonCauseMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonCauseMapResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonCauseMap;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalReasonCauseMapMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalReasonCauseMapRepository;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.repository.partial.PartialCauseRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalReasonCauseMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalReasonCauseMapServiceImpl
        implements PartialWithdrawalReasonCauseMapService {

    private final PartialWithdrawalReasonCauseMapRepository repository;
    private final PartialWithdrawalReasonCauseMapMapper mapper;

    private final PartialReasonRepository reasonRepo;
    private final PartialCauseRepository causeRepo;

    // -----------------------
    // CREATE
    // -----------------------
    @Override
    public PartialWithdrawalReasonCauseMapResponseDto create(PartialWithdrawalReasonCauseMapRequestDto dto) {

        PartialWithdrawalReasonCauseMap entity = mapper.toEntity(dto);

        entity.setReason(
                reasonRepo.findById(dto.getReasonId())
                        .orElseThrow(() ->
                                ClaimException.resourceNotFound("Reason", String.valueOf(dto.getReasonId()))
                        )
        );

        entity.setCause(
                causeRepo.findById(dto.getCauseId())
                        .orElseThrow(() ->
                                ClaimException.resourceNotFound("Cause", String.valueOf(dto.getCauseId()))
                        )
        );

        return mapper.toDto(repository.save(entity));
    }

    // -----------------------
    // UPDATE (PATCH)
    // -----------------------
    @Override
    public PartialWithdrawalReasonCauseMapResponseDto update(Long id,
                                                             PartialWithdrawalReasonCauseMapRequestDto dto) {

        PartialWithdrawalReasonCauseMap entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalReasonCauseMap", String.valueOf(id))
                );

        mapper.updateEntity(dto, entity);

        if (dto.getReasonId() != null) {
            entity.setReason(
                    reasonRepo.findById(dto.getReasonId())
                            .orElseThrow(() ->
                                    ClaimException.resourceNotFound("Reason", String.valueOf(dto.getReasonId()))
                            )
            );
        }

        if (dto.getCauseId() != null) {
            entity.setCause(
                    causeRepo.findById(dto.getCauseId())
                            .orElseThrow(() ->
                                    ClaimException.resourceNotFound("Cause", String.valueOf(dto.getCauseId()))
                            )
            );
        }

        return mapper.toDto(repository.save(entity));
    }

    // -----------------------
    // GET BY ID
    // -----------------------
    @Override
    public PartialWithdrawalReasonCauseMapResponseDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalReasonCauseMap", String.valueOf(id))
                );
    }

    // -----------------------
    // GET ALL
    // -----------------------
    @Override
    public List<PartialWithdrawalReasonCauseMapResponseDto> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    // -----------------------
    // FK FILTERS
    // -----------------------
    @Override
    public List<PartialWithdrawalReasonCauseMapResponseDto> getByCauseId(Long causeId) {

        // 🔴 VALIDATE FK FIRST
        if (!causeRepo.existsById(causeId)) {
            throw ClaimException.resourceNotFound("Cause", String.valueOf(causeId));
        }

        return mapper.toDtoList(repository.findByCause_Id(causeId));
    }

    @Override
    public List<PartialWithdrawalReasonCauseMapResponseDto> getByReasonId(Long reasonId) {

        if (!reasonRepo.existsById(reasonId)) {
            throw ClaimException.resourceNotFound("Reason", String.valueOf(reasonId));
        }

        return mapper.toDtoList(repository.findByReason_Id(reasonId));
    }
    // -----------------------
    // DELETE
    // -----------------------
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw ClaimException.resourceNotFound(
                    "PartialWithdrawalReasonCauseMap",
                    String.valueOf(id)
            );
        }
        repository.deleteById(id);
    }
}