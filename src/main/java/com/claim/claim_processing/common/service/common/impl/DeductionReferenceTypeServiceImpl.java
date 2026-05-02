package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionReferenceTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.DeductionReferenceTypeMapper;
import com.claim.claim_processing.common.repository.common.DeductionReferenceTypeRepository;
import com.claim.claim_processing.common.service.common.DeductionReferenceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionReferenceTypeServiceImpl implements DeductionReferenceTypeService {

    private final DeductionReferenceTypeRepository repository;
    private final DeductionReferenceTypeMapper mapper;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @Override
    public DeductionReferenceTypeResponseDto create(
            DeductionReferenceTypeRequestDto dto
    ) {

        if (dto.getCode() != null &&
                repository.existsByCode(dto.getCode().trim())) {

            throw new RuntimeException(
                    "Deduction Reference Type code already exists: " + dto.getCode()
            );
        }

        DeductionReferenceTypeMaster entity = mapper.toEntity(dto);

        DeductionReferenceTypeMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // -------------------------------------------------
    // UPDATE (FIXED)
    // -------------------------------------------------
    @Override
    public DeductionReferenceTypeResponseDto update(
            Long id,
            DeductionReferenceTypeRequestDto dto
    ) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Deduction Reference Type not found with id: " + id
                ));

        // duplicate code check only if code provided
        if (dto.getCode() != null &&
                repository.existsByCodeAndIdNot(dto.getCode().trim(), id)) {

            throw new RuntimeException(
                    "Deduction Reference Type code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntityFromDto(dto, entity);

        DeductionReferenceTypeMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // -------------------------------------------------
    // GET BY ID
    // -------------------------------------------------
    @Override
    public DeductionReferenceTypeResponseDto getById(Long id) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Deduction Reference Type not found with id: " + id
                ));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------------------------
    // GET BY CODE
    // -------------------------------------------------
    @Override
    public DeductionReferenceTypeResponseDto getByCode(String code) {

        DeductionReferenceTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(
                        "Deduction Reference Type not found with code: " + code
                ));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @Override
    public List<DeductionReferenceTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------------------------
    // GET ACTIVE ONLY
    // -------------------------------------------------
    @Override
    public List<DeductionReferenceTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findAllByIsActive(ActivityEnum.Y)
        );
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @Override
    public void delete(Long id) {

        DeductionReferenceTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Deduction Reference Type not found with id: " + id
                ));

        repository.delete(entity);
    }
}