package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.SubmissionChannelMapper;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import com.claim.claim_processing.common.service.common.SubmissionChannelService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionChannelServiceImpl implements SubmissionChannelService {

    private final SubmissionChannelRepository repository;
    private final SubmissionChannelMapper mapper;

    @Override
    public ApiResponseDTO<SubmissionChannelResponseDto> create(
            SubmissionChannelRequestDto requestDto
    ) {

        try {

            if (repository.existsByCode(requestDto.getCode())) {
                throw ClaimException.conflict(
                        "Submission Channel code already exists: "
                                + requestDto.getCode()
                );
            }

            SubmissionChannelMaster entity =
                    mapper.toEntity(requestDto);

            entity.setCreatedBy(requestDto.getCreatedBy());
            entity.setUpdatedBy(requestDto.getUpdatedBy());

            SubmissionChannelMaster savedEntity =
                    repository.save(entity);

            return ApiResponseDTO.created(
                    mapper.toResponseDto(savedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error creating Submission Channel", ex);

            throw ClaimException.internalError(
                    "Failed to create Submission Channel",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<SubmissionChannelResponseDto> update(
            Long id,
            SubmissionChannelUpdateDto updateDto
    ) {

        try {

            SubmissionChannelMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Submission Channel",
                                    String.valueOf(id)
                            )
                    );

            mapper.updateEntityFromDto(updateDto, entity);

            entity.setUpdatedBy(updateDto.getUpdatedBy());

            SubmissionChannelMaster updatedEntity =
                    repository.save(entity);

            return ApiResponseDTO.success(
                    "Submission Channel updated successfully",
                    mapper.toResponseDto(updatedEntity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error updating Submission Channel", ex);

            throw ClaimException.internalError(
                    "Failed to update Submission Channel",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<SubmissionChannelResponseDto> getById(
            Long id
    ) {

        try {

            SubmissionChannelMaster entity = repository.findById(id)
                    .orElseThrow(() ->
                            ClaimException.resourceNotFound(
                                    "Submission Channel",
                                    String.valueOf(id)
                            )
                    );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error fetching Submission Channel by id", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Submission Channel",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<SubmissionChannelResponseDto> getByCode(
            String code
    ) {

        try {

            SubmissionChannelMaster entity =
                    repository.findByCode(code)
                            .orElseThrow(() ->
                                    ClaimException.resourceNotFound(
                                            "Submission Channel code",
                                            code
                                    )
                            );

            return ApiResponseDTO.success(
                    mapper.toResponseDto(entity)
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error fetching Submission Channel by code", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Submission Channel",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<List<SubmissionChannelResponseDto>> getAll() {

        try {

            List<SubmissionChannelResponseDto> response =
                    repository.findAll()
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching all Submission Channels", ex);

            throw ClaimException.internalError(
                    "Failed to fetch Submission Channels",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<List<SubmissionChannelResponseDto>> getAllActive() {

        try {

            List<SubmissionChannelResponseDto> response =
                    repository.findByIsActive(ActivityEnum.Y)
                            .stream()
                            .map(mapper::toResponseDto)
                            .toList();

            return ApiResponseDTO.success(response);

        } catch (Exception ex) {

            log.error("Error fetching active Submission Channels", ex);

            throw ClaimException.internalError(
                    "Failed to fetch active Submission Channels",
                    ex
            );
        }
    }

    @Override
    public ApiResponseDTO<String> delete(
            Long id
    ) {

        try {

            SubmissionChannelMaster entity =
                    repository.findById(id)
                            .orElseThrow(() ->
                                    ClaimException.resourceNotFound(
                                            "Submission Channel",
                                            String.valueOf(id)
                                    )
                            );

            entity.setIsActive(ActivityEnum.N);
            entity.setUpdatedAt(LocalDateTime.now());

            repository.save(entity);

            return ApiResponseDTO.success(
                    "Submission Channel deleted successfully",
                    null
            );

        } catch (ClaimException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error deleting Submission Channel", ex);

            throw ClaimException.internalError(
                    "Failed to delete Submission Channel",
                    ex
            );
        }
    }
}