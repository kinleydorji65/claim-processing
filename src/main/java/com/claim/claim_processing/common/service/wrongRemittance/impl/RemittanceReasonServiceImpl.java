package com.claim.claim_processing.common.service.wrongRemittance.impl;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrongRemittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceReasonMaster;
import com.claim.claim_processing.common.mapper.wrongRemittance.RemittanceReasonMapper;
import com.claim.claim_processing.common.repository.wrongRemittance.RemittanceReasonRepository;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemittanceReasonServiceImpl implements RemittanceReasonService {



    private final RemittanceReasonRepository repository;
    private final RemittanceReasonMapper mapper;

    @Override
    public ApiResponseDTO<RemittanceReasonResponseDto> create(RemittanceReasonRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Remittance reason code already exists: " + requestDto.getCode()
            );
        }

        WrongRemittanceReasonMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy(requestDto.getCreatedBy());

        WrongRemittanceReasonMaster saved = repository.save(entity);
        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    @Override
    public ApiResponseDTO<RemittanceReasonResponseDto> getById(Long id) {
        return ApiResponseDTO.success(mapper.toResponseDto(findById(id)));
    }

    @Override
    public ApiResponseDTO<RemittanceReasonResponseDto> getByCode(String code) {
        WrongRemittanceReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Remittance reason not found with code: " + code)
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    @Override
    public ApiResponseDTO<List<RemittanceReasonResponseDto>> getAll() {
        return ApiResponseDTO.success(mapper.toResponseDtoList(repository.findAll()));
    }

    @Override
    public ApiResponseDTO<List<RemittanceReasonResponseDto>> getAllActive() {
        return ApiResponseDTO.success(mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
        ));
    }

    @Override
    public ApiResponseDTO<RemittanceReasonResponseDto> update(Long id, RemittanceReasonUpdateDto updateDto) {
        WrongRemittanceReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy(updateDto.getUpdatedBy());
        entity.setUpdatedBy("SYSTEM");

        WrongRemittanceReasonMaster updated = repository.save(entity);
        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    @Override
    public ApiResponseDTO<String> deactivate(Long id) {
        WrongRemittanceReasonMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
        return ApiResponseDTO.success("Remittance reason deactivated successfully");
    }

    private WrongRemittanceReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Remittance reason not found with id: " + id)
                );
    }

    @Override
    public ApiResponseDTO<String> delete(Long id) {

        WrongRemittanceReasonMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Remittance reason not found with id: " + id
                        )
                );

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Remittance reason deleted successfully",
                "Deleted successfully"
        );
    }
}