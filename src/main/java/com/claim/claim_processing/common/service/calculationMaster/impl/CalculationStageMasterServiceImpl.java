package com.claim.claim_processing.common.service.calculationMaster.impl;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationStageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.calculationMaster.CalculationStageMasterMapper;
import com.claim.claim_processing.common.repository.calculationMaster.CalculationStageMasterRepository;
import com.claim.claim_processing.common.service.calculationMaster.CalculationStageMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationStageMasterServiceImpl implements CalculationStageMasterService {

    private final CalculationStageMasterRepository repository;
    private final CalculationStageMasterMapper mapper;

    @Override
    public CalculationStageResponseDto create(CalculationStageRequestDto request) {

        if (repository.existsByCode(request.getCode())) {
            throw new RuntimeException("Code already exists: " + request.getCode());
        }

        CalculationStageMaster entity = mapper.toEntity(request);
        CalculationStageMaster saved = repository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public CalculationStageResponseDto update(Long id, CalculationStageRequestDto request) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new RuntimeException("Code already exists: " + request.getCode());
        }

        mapper.updateFromDto(request, entity);

        CalculationStageMaster updated = repository.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    public CalculationStageResponseDto getById(Long id) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        return mapper.toDto(entity);
    }

    @Override
    public CalculationStageResponseDto getByCode(String code) {

        CalculationStageMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Record not found with code: " + code));

        return mapper.toDto(entity);
    }

    @Override
    public List<CalculationStageResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {

        CalculationStageMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        repository.delete(entity);
    }

    @Override
    public List<CalculationStageResponseDto> getAllActive() {

        return repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}