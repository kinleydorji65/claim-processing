package com.claim.claim_processing.common.service.specialCase.impl;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.mapper.specialCase.SpecialCaseRefundReasonMasterMapper;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseRefundReasonMasterRepository;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseRefundReasonMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialCaseRefundReasonMasterServiceImpl implements SpecialCaseRefundReasonMasterService {

    private final SpecialCaseRefundReasonMasterRepository repository;
    private final SpecialCaseRefundReasonMasterMapper mapper;

    @Override
    public SpecialCaseRefundReasonResponseDto create(
            SpecialCaseRefundReasonRequestDto requestDto
    ) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Special case refund reason code already exists.");
        }

        if (repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Special case refund reason name already exists.");
        }

        SpecialCaseRefundReasonMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public SpecialCaseRefundReasonResponseDto update(
            Long id,
            SpecialCaseRefundReasonRequestDto requestDto
    ) {

        SpecialCaseRefundReasonMaster entity = getEntityById(id);

        if (!entity.getCode().equals(requestDto.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Special case refund reason code already exists.");
        }

        if (!entity.getName().equals(requestDto.getName())
                && repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Special case refund reason name already exists.");
        }

        mapper.updateEntityFromDto(requestDto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public SpecialCaseRefundReasonResponseDto patch(
            Long id,
            SpecialCaseRefundReasonRequestDto requestDto
    ) {

        SpecialCaseRefundReasonMaster entity = getEntityById(id);

        if (requestDto.getCode() != null
                && !entity.getCode().equals(requestDto.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Special case refund reason code already exists.");
        }

        if (requestDto.getName() != null
                && !entity.getName().equals(requestDto.getName())
                && repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Special case refund reason name already exists.");
        }

        mapper.patchEntityFromDto(requestDto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialCaseRefundReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialCaseRefundReasonResponseDto getByCode(String code) {

        SpecialCaseRefundReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Special case refund reason not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialCaseRefundReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialCaseRefundReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    @Override
    public void delete(Long id) {

        SpecialCaseRefundReasonMaster entity = getEntityById(id);

        if (entity.getIsActive() == ActivityEnum.N) {
            throw ClaimException.conflict("Special case refund reason already deleted.");
        }

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);
    }

    private SpecialCaseRefundReasonMaster getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Special case refund reason not found with id: " + id)
                );
    }
}