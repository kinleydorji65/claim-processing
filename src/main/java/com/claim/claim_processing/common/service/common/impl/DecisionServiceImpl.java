package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.DecisionMapper;
import com.claim.claim_processing.common.repository.common.DecisionRepository;
import com.claim.claim_processing.common.service.common.DecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionServiceImpl implements DecisionService {

    private final DecisionRepository repository;
    private final DecisionMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public DecisionResponseDto createDecision(DecisionRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Decision code already exists: " + dto.getCode());
        }

        DecisionMaster entity = mapper.toEntity(dto);

        DecisionMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @Override
    public DecisionResponseDto updateDecision(Long id, DecisionRequestDto dto) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decision not found with id: " + id));

        // PATCH update (only non-null fields)
        mapper.updateEntityFromDto(dto, entity);

        DecisionMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    public DecisionResponseDto getById(Long id) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decision not found with id: " + id));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // GET BY CODE
    // -------------------------------
    @Override
    public DecisionResponseDto getByCode(String code) {

        DecisionMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Decision not found with code: " + code));

        return mapper.toResponseDto(entity);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    public List<DecisionResponseDto> getAll() {

        return mapper.toResponseDtoList(repository.findAll());
    }

    // -------------------------------
    // GET ACTIVE ONLY
    // -------------------------------
    @Override
    public List<DecisionResponseDto> getAllActive() {

        return mapper.toResponseDtoList(
                repository.findAllByIsActive(ActivityEnum.Y)
        );
    }

    // -------------------------------
    // DELETE (soft delete recommended later)
    // -------------------------------
    @Override
    public void deleteDecision(Long id) {

        DecisionMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decision not found with id: " + id));

        repository.delete(entity);
    }
}