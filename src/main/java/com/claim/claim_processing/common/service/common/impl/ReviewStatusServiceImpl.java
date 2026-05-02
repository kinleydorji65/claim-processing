package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ReviewStatusMapper;
import com.claim.claim_processing.common.repository.common.ReviewStatusRepository;
import com.claim.claim_processing.common.service.common.ReviewStatusService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewStatusServiceImpl implements ReviewStatusService {

    private final ReviewStatusRepository repository;
    private final ReviewStatusMapper mapper;

    // 🔹 CREATE
    @Override
    public ReviewStatusResponseDto create(ReviewStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Review Status already exists with code: " + dto.getCode()
            );
        }

        ReviewStatusMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 UPDATE
    @Override
    public ReviewStatusResponseDto update(Long id, ReviewStatusRequestDto dto) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("ReviewStatus", id.toString())
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    // 🔹 GET BY ID
    @Override
    @Transactional(readOnly = true)
    public ReviewStatusResponseDto getById(Long id) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("ReviewStatus", id.toString())
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET BY CODE
    @Override
    @Transactional(readOnly = true)
    public ReviewStatusResponseDto getByCode(String code) {

        ReviewStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Review Status not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    // 🔹 GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<ReviewStatusResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // 🔹 GET ACTIVE
    @Override
    @Transactional(readOnly = true)
    public List<ReviewStatusResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    // 🔹 DELETE (SOFT DELETE)
    @Override
    public void delete(Long id) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("ReviewStatus", id.toString())
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}