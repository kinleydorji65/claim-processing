package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalCauseMapper;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalCauseRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalCauseService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalCauseServiceImpl implements PartialWithdrawalCauseService {

    private final PartialWithdrawalCauseRepository causeRepository;
    private final PartialReasonRepository reasonRepository;
    private final PartialWithdrawalCauseMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public PartialWithdrawalCauseResponseDto create(PartialWithdrawalCauseRequestDto dto) {

        if (causeRepository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Cause code already exists: " + dto.getCode());
        }

        PartialWithdrawalCauseMaster entity = mapper.toEntity(dto);

        PartialWithdrawalReasonMaster reason = reasonRepository.findById(dto.getReasonId())
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalReason", String.valueOf(dto.getReasonId()))
                );

        entity.setReason(reason);

        PartialWithdrawalCauseMaster saved = causeRepository.save(entity);

        return mapper.toDto(saved);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public PartialWithdrawalCauseResponseDto update(Long id, PartialWithdrawalCauseRequestDto dto) {

        PartialWithdrawalCauseMaster entity = causeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalCause", String.valueOf(id))
                );

        mapper.updateEntity(entity, dto);

        if (dto.getReasonId() != null) {
            PartialWithdrawalReasonMaster reason = reasonRepository.findById(dto.getReasonId())
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound("PartialWithdrawalReason", String.valueOf(dto.getReasonId()))
                    );
            entity.setReason(reason);
        }

        return mapper.toDto(causeRepository.save(entity));
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public PartialWithdrawalCauseResponseDto getById(Long id) {

        PartialWithdrawalCauseMaster entity = causeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalCause", String.valueOf(id))
                );

        return mapper.toDto(entity);
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    public List<PartialWithdrawalCauseResponseDto> getAll() {
        return causeRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================
    // GET BY REASON ID
    // =========================
    @Override
    public List<PartialWithdrawalCauseResponseDto> getByReasonId(Long reasonId) {
        return causeRepository.findByReason_Id(reasonId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================
    // DELETE (SOFT DELETE)
    // =========================
    @Override
    public void delete(Long id) {

        PartialWithdrawalCauseMaster entity = causeRepository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("PartialWithdrawalCause", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);

        causeRepository.save(entity);
    }
}