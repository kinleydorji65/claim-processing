package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.ClaimTypeMasterMapper;
import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
import com.claim.claim_processing.common.service.claim.ClaimTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimTypeMasterServiceImpl implements ClaimTypeMasterService {

    private final ClaimTypeMasterRepository repository;
    private final ClaimTypeMasterMapper mapper;

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto create(ClaimTypeMasterRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Claim Type code already exists: " + requestDto.getCode());
        }

        ClaimTypeMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto update(Long id, ClaimTypeMasterRequestDto requestDto) {

        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        mapper.updateEntityFromDto(requestDto, entity);

        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponseDto(repository.save(entity));
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto getById(Long id) {
        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        return mapper.toResponseDto(entity);
    }

    // -----------------------------
    // GET BY CODE (IMPORTANT FOR RULE ENGINE)
    // -----------------------------
    @Override
    public ClaimTypeMasterResponseDto getByCode(String code) {
        ClaimTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with code: " + code));

        return mapper.toResponseDto(entity);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @Override
    public List<ClaimTypeMasterResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public List<ClaimTypeMasterResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActive(ActivityEnum.Y));
    }

    // -----------------------------
    // DELETE (soft delete recommended in future)
    // -----------------------------
    @Override
    public void delete(Long id) {
        ClaimTypeMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim Type not found with id: " + id));

        repository.delete(entity);
    }
}