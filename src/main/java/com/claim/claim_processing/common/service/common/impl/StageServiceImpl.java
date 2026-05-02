package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.StageMapper;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.service.common.StageService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StageServiceImpl implements StageService {

    private final StageRepository repository;
    private final StageMapper mapper;

    // 🔹 CREATE
    @Override
    public StageResponseDto create(StageRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Stage already exists with code: " + dto.getCode()
            );
        }

        StageMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 UPDATE
    @Override
    public StageResponseDto update(Long id, StageRequestDto dto) {

        StageMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Stage", id.toString())
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public StageResponseDto getById(Long id) {

        StageMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Stage", id.toString())
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET BY CODE
    @Override
    @Transactional(readOnly = true)
    public StageResponseDto getByCode(String code) {

        StageMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Stage not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<StageResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<StageResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        StageMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Stage", id.toString())
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}