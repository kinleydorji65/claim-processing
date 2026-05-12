package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.claim.TerminationReasonMapper;
import com.claim.claim_processing.common.repository.claim.TerminationReasonRepository;
import com.claim.claim_processing.common.service.claim.TerminationReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerminationReasonServiceImpl implements TerminationReasonService {

    private final TerminationReasonRepository repository;
    private final TerminationReasonMapper mapper;

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @Override
    public ApiResponseDTO<List<TerminationReasonResponseDto>> getAllActive() {

        List<TerminationReasonResponseDto> response =
                repository.findByIsActive(ActivityEnum.Y)
                        .stream()
                        .map(mapper::toResponseDto)
                        .toList();

        if (response.isEmpty()) {
            throw ClaimException.notFound("No active Termination Reasons found");
        }

        return ApiResponseDTO.success(
                "Termination Reasons fetched successfully",
                response
        );
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @Override
    public ApiResponseDTO<TerminationReasonResponseDto> getById(Long id) {

        TerminationReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Termination Reason",
                                String.valueOf(id)
                        )
                );

        return ApiResponseDTO.success(
                "Termination Reason fetched successfully",
                mapper.toResponseDto(entity)
        );
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @Override
    public ApiResponseDTO<TerminationReasonResponseDto> create(
            TerminationReasonCreateRequestDto requestDto
    ) {

        try {

            TerminationReasonMaster entity = mapper.toEntity(requestDto);

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            if (entity.getIsActive() == null) {
                entity.setIsActive(ActivityEnum.Y);
            }

            TerminationReasonMaster saved = repository.save(entity);

            return ApiResponseDTO.success(
                    "Termination Reason created successfully",
                    mapper.toResponseDto(saved)
            );

        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to create Termination Reason",
                    ex
            );
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @Override
    public ApiResponseDTO<TerminationReasonResponseDto> update(
            Long id,
            TerminationReasonUpdateRequestDto requestDto
    ) {

        try {

            TerminationReasonMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Termination Reason",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(requestDto, entity);

            entity.setUpdatedAt(LocalDateTime.now());

            TerminationReasonMaster updated = repository.save(entity);

            return ApiResponseDTO.success(
                    "Termination Reason updated successfully",
                    mapper.toResponseDto(updated)
            );

        } catch (Exception ex) {
            throw ClaimException.internalError(
                    "Failed to update Termination Reason",
                    ex
            );
        }
    }

    // -----------------------------
    // DEACTIVATE
    // -----------------------------
    @Override
    public ApiResponseDTO<String> deactivate(Long id) {

        TerminationReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Termination Reason",
                                String.valueOf(id)
                        )
                );

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return ApiResponseDTO.success(
                "Termination Reason deactivated successfully"
        );
    }
}