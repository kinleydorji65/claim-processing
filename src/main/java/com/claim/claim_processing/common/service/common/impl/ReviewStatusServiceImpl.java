package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.common.mapper.common.ReviewStatusMapper;
import com.claim.claim_processing.common.repository.common.ReviewStatusRepository;
import com.claim.claim_processing.common.service.common.ReviewStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewStatusServiceImpl implements ReviewStatusService {

    private final ReviewStatusRepository repository;
    private final ReviewStatusMapper mapper;

    // ---------------- CREATE ----------------
    @Override
    public ApiResponseDTO<ReviewStatusResponseDto> create(ReviewStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict("Review Status code already exists: " + dto.getCode());
        }

        ReviewStatusMaster entity = mapper.toEntity(dto);
        ReviewStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.created(mapper.toResponseDto(saved));
    }

    // ---------------- UPDATE (PATCH) ----------------
    @Override
    public ApiResponseDTO<ReviewStatusResponseDto> update(Long id, ReviewStatusRequestDto dto) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Review Status", String.valueOf(id))
                );

        mapper.updateEntityFromDto(dto, entity);

        ReviewStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ---------------- GET BY ID ----------------
    @Override
    public ApiResponseDTO<ReviewStatusResponseDto> getById(Long id) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Review Status", String.valueOf(id))
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ---------------- GET BY CODE ----------------
    @Override
    public ApiResponseDTO<ReviewStatusResponseDto> getByCode(String code) {

        ReviewStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Review Status", code)
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ---------------- GET ALL ----------------
    @Override
    public ApiResponseDTO<List<ReviewStatusResponseDto>> getAll() {

        List<ReviewStatusResponseDto> list = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(list);
    }

    // ---------------- GET ALL ACTIVE ----------------
    @Override
    public ApiResponseDTO<List<ReviewStatusResponseDto>> getAllActive() {

        List<ReviewStatusResponseDto> list = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(list);
    }

    // ---------------- SOFT DELETE ----------------
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        ReviewStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound("Review Status", String.valueOf(id))
                );

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);

        return ApiResponseDTO.success("Review Status deactivated successfully");
    }
}