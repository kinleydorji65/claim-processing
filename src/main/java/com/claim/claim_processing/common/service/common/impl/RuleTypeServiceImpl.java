package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.RuleTypeMapper;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.service.common.RuleTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RuleTypeServiceImpl implements RuleTypeService {

    private final RuleTypeRepository repository;
    private final RuleTypeMapper mapper;

    // 🔹 CREATE
    @Override
    public RuleTypeResponseDto create(RuleTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Rule Type already exists with code: " + dto.getCode()
            );
        }

        RuleTypeMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getDisplayOrder() != null) {
            entity.setDisplayOrder(dto.getDisplayOrder());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 UPDATE
    @Override
    public RuleTypeResponseDto update(Long id, RuleTypeRequestDto dto) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("RuleType", id.toString())
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public RuleTypeResponseDto getById(Long id) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("RuleType", id.toString())
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET BY CODE
    @Override
    @Transactional(readOnly = true)
    public RuleTypeResponseDto getByCode(String code) {

        RuleTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rule Type not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<RuleTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(
                repository.findAllByOrderByDisplayOrderAsc()
        );
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<RuleTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAsc(ActivityEnum.Y)
        );
    }

    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        RuleTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("RuleType", id.toString())
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}