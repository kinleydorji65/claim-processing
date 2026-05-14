package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.mapper.partial.PartialWithdrawalCauseMapper;
import com.claim.claim_processing.common.repository.partial.PartialWithdrawalCauseRepository;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalCauseService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalCauseServiceImpl implements PartialWithdrawalCauseService {

    private final PartialWithdrawalCauseRepository repository;
    private final PartialReasonRepository reasonRepository;
    private final PartialWithdrawalCauseMapper mapper;

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> create(
            PartialWithdrawalCauseRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Partial Withdrawal Cause code already exists: " + requestDto.getCode()
            );
        }

        PartialWithdrawalCauseMaster entity = mapper.toEntity(requestDto);

        entity.setReason(getReason(requestDto.getReasonId()));
        entity.setCreatedBy(requestDto.getCreatedBy());
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> getById(Long id) {

        PartialWithdrawalCauseMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Cause not found with id: " + id
                        ));

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> getByCode(String code) {

        PartialWithdrawalCauseMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Cause not found with code: " + code
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
    public ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getByReason_Id(Long reasonId) {

        getReason(reasonId);

        List<PartialWithdrawalCauseResponseDto> responseDtos = repository.findByReason_Id(reasonId)
                .stream()
                .map(mapper::toResponseDto)
                .toList();

        if (responseDtos.isEmpty()) {
            throw ClaimException.notFound(
                    "No Partial Withdrawal Causes found for reason id: " + reasonId
            );
        }

        return ApiResponseDTO.success(responseDtos);
    }

    @Override
    public ApiResponseDTO<PartialWithdrawalCauseResponseDto> update(
            Long id,
            PartialWithdrawalCauseRequestDto updateDto) {

        PartialWithdrawalCauseMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Partial Withdrawal Cause not found with id: " + id
                        ));

        mapper.updateEntityFromDto(updateDto, entity);

        if (updateDto.getReasonId() != null) {
            entity.setReason(getReason(updateDto.getReasonId()));
        }

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
                                "Partial Withdrawal Cause not found with id: " + id
                        ));

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Partial Withdrawal Cause deleted successfully"
        );
    }

    private PartialWithdrawalReasonMaster getReason(Long reasonId) {

        if (reasonId == null) {
            throw ClaimException.badRequest("Reason id is required");
        }

        return reasonRepository.findById(reasonId)
                .orElseThrow(() ->
                        ClaimException.resourceNotFound(
                                "Partial Withdrawal Reason",
                                String.valueOf(reasonId)
                        ));
    }
}