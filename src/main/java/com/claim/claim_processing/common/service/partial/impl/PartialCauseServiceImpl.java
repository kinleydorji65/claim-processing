package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.mapper.partial.PartialCauseMapper;
import com.claim.claim_processing.common.repository.partial.PartialCauseRepository;
import com.claim.claim_processing.common.service.partial.PartialCauseService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialCauseServiceImpl implements PartialCauseService {

    private final PartialCauseRepository repository;
    private final PartialCauseMapper mapper;

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> create(
            PartialCauseRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Partial Cause code already exists: " + requestDto.getCode()
            );
        }

        PartialWithdrawalCauseMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getCreatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> getById(Long id) {

        PartialWithdrawalCauseMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Cause not found with id: " + id
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> getByCode(String code) {

        PartialWithdrawalCauseMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Cause not found with code: " + code
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAll() {

        List<PartialWithdrawalCauseResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAllActive() {

        List<PartialWithdrawalCauseResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> update(
            Long id,
            PartialCauseUpdateDto updateDto) {

        PartialWithdrawalCauseMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Cause not found with id: " + id
                        ));

        mapper.updateEntityFromDto(updateDto, entity);

        entity.setUpdatedBy(updateDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PartialWithdrawalCauseMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Cause not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Partial Cause deleted successfully"
        );
    }
}