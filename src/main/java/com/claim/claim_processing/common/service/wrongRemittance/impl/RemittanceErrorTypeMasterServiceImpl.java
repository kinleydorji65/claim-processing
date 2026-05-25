package com.claim.claim_processing.common.service.wrongRemittance.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;
import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceErrorTypeMaster;
import com.claim.claim_processing.common.mapper.wrongRemittance.RemittanceErrorTypeMasterMapper;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceErrorTypeMasterService;
import com.claim.claim_processing.common.repository.wrongRemittance.RemittanceErrorTypeMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemittanceErrorTypeMasterServiceImpl
        implements RemittanceErrorTypeMasterService {

    private final RemittanceErrorTypeMasterRepository repository;
    private final RemittanceErrorTypeMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<RemittanceErrorTypeResponseDto> create(
            RemittanceErrorTypeRequestDto dto
    ) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Wrong remittance error type code already exists: " + dto.getCode()
            );
        }

        WrongRemittanceErrorTypeMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        WrongRemittanceErrorTypeMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<RemittanceErrorTypeResponseDto> update(
            Long id,
            RemittanceErrorTypeRequestDto dto
    ) {

        WrongRemittanceErrorTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Wrong remittance error type not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Wrong remittance error type code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);
        entity.setUpdatedBy(dto.getUpdatedBy());

        WrongRemittanceErrorTypeMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<RemittanceErrorTypeResponseDto> getById(Long id) {

        WrongRemittanceErrorTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Wrong remittance error type not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<RemittanceErrorTypeResponseDto> getByCode(String code) {

        WrongRemittanceErrorTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Wrong remittance error type not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> getAll() {

        List<WrongRemittanceErrorTypeMaster> list = repository.findAll();

        list.sort(
                Comparator.comparing(
                        WrongRemittanceErrorTypeMaster::getDisplayOrder
                )
        );

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> getAllActive() {

        List<WrongRemittanceErrorTypeMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(
                Comparator.comparing(
                        WrongRemittanceErrorTypeMaster::getDisplayOrder
                )
        );

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        WrongRemittanceErrorTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Wrong remittance error type not found with id: " + id
                        )
                );

        repository.delete(entity);

        return ApiResponseDTO.success(
                "Wrong remittance error type deleted successfully",
                "Deleted successfully"
        );
    }
}