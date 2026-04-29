package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.DeductionTypeMapper;
import com.claim.claim_processing.common.repository.common.DeductionTypeRepository;
import com.claim.claim_processing.common.service.common.DeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionTypeServiceImpl implements DeductionTypeService {

    private final DeductionTypeRepository repository;
    private final DeductionTypeMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public DeductionTypeResponseDto create(DeductionTypeRequestDto dto) {

        // duplicate check
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Deduction type code already exists: " + dto.getCode());
        }

        DeductionTypeMaster entity = mapper.toEntity(dto);

        DeductionTypeMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public DeductionTypeResponseDto getById(Long id) {

        DeductionTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction type not found: " + id));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @Override
    public List<DeductionTypeResponseDto> getAllActive() {

        List<DeductionTypeMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        return mapper.toResponseDtoList(list);
    }

    // -------------------------------
    // UPDATE (PATCH STYLE)
    // -------------------------------
    @Override
    public DeductionTypeResponseDto update(Long id, DeductionTypeRequestDto dto) {

        DeductionTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction type not found: " + id));

        // if code is changing → validate uniqueness
        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw new RuntimeException("Deduction type code already exists: " + dto.getCode());
        }

        mapper.updateEntityFromDto(dto, entity);

        DeductionTypeMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // -------------------------------
    // DELETE (soft delete style recommended)
    // -------------------------------
    @Override
    public void delete(Long id) {

        DeductionTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction type not found: " + id));

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }

    @Override
    public DeductionTypeResponseDto getByCode(String code) {

        DeductionTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Deduction type not found: " + code));

        return mapper.toResponseDto(entity);
    }
}