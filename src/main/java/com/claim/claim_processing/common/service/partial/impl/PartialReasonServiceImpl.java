package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.mapper.partial.PartialReasonMapper;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.service.partial.PartialReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialReasonServiceImpl implements PartialReasonService {

    private final PartialReasonRepository repository;
    private final PartialReasonMapper mapper;

    @Override
    public ApiResponseDTO<PartialWithdrawalReasonResponseDto> create(
            PartialWithdrawalReasonRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Partial Withdrawal Reason code already exists: " + requestDto.getCode()
            );
        }

        PartialWithdrawalReasonMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedBy(requestDto.getCreatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalReasonResponseDto> getById(Long id) {

        PartialWithdrawalReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Reason not found with id: " + id
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalReasonResponseDto> getByCode(String code) {

        PartialWithdrawalReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Reason not found with code: " + code
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<PartialWithdrawalReasonResponseDto>> getAll() {

        List<PartialWithdrawalReasonResponseDto> responseDtos = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<List<PartialWithdrawalReasonResponseDto>> getAllActive() {

        List<PartialWithdrawalReasonResponseDto> responseDtos = repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalReasonResponseDto> update(
            Long id,
            PartialWithdrawalReasonUpdateDto updateDto) {

        PartialWithdrawalReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Reason not found with id: " + id
                        ));

        mapper.updateEntityFromDto(updateDto, entity);

        entity.setUpdatedBy(updateDto.getUpdatedBy());

        return ApiResponseDTO.success(
                mapper.toResponseDto(repository.save(entity))
        );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        PartialWithdrawalReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Reason not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Partial Withdrawal Reason deleted successfully"
        );
    }
}