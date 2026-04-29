package com.claim.claim_processing.common.service.calculationMaster.impl;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationTriggerTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.calculationMaster.CalculationTriggerTypeMapper;
import com.claim.claim_processing.common.repository.calculationMaster.CalculationTriggerTypeRepository;
import com.claim.claim_processing.common.service.calculationMaster.CalculationTriggerTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalculationTriggerTypeServiceImpl implements CalculationTriggerTypeService {

    private final CalculationTriggerTypeRepository repository;
    private final CalculationTriggerTypeMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public CalculationTriggerTypeResponseDto create(CalculationTriggerTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Code already exists: " + dto.getCode());
        }

        CalculationTriggerTypeMaster entity = mapper.toEntity(dto);

        entity.setIsActive(ActivityEnum.Y);

        return mapper.toDto(repository.save(entity));
    }


    // -------------------------------
    // PATCH (PARTIAL UPDATE)
    // -------------------------------
    @Override
    public CalculationTriggerTypeResponseDto patch(CalculationTriggerTypeRequestDto dto) {

        CalculationTriggerTypeMaster entity = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Trigger Type not found: " + dto.getId()));

        mapper.patchEntityFromDto(dto, entity);

        return mapper.toDto(repository.save(entity));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public CalculationTriggerTypeResponseDto getById(Long id) {

        CalculationTriggerTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trigger Type not found: " + id));

        return mapper.toDto(entity);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CalculationTriggerTypeResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CalculationTriggerTypeResponseDto> getAllActive() {

        return repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // -------------------------------
    // SOFT DELETE (recommended)
    // -------------------------------
    @Override
    public void delete(Long id) {

        CalculationTriggerTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trigger Type not found: " + id));

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}