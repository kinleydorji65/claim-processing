package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.RentClearanceStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.RentClearanceStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.RentClearanceStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.RentClearanceStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentClearanceStatusMasterServiceImpl implements RentClearanceStatusMasterService {

    private final RentClearanceStatusMasterRepository repository;
    private final RentClearanceStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public ApiResponseDTO<RentClearanceStatusResponseDto> create(RentClearanceStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Rent clearance status code already exists: " + dto.getCode()
            );
        }

        RentClearanceStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        RentClearanceStatusMaster saved = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(saved));
    }

    // ================= UPDATE =================
    @Override
    public ApiResponseDTO<RentClearanceStatusResponseDto> update(Long id, RentClearanceStatusRequestDto dto) {

        RentClearanceStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rent clearance status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Rent clearance status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        RentClearanceStatusMaster updated = repository.save(entity);

        return ApiResponseDTO.success(mapper.toResponseDto(updated));
    }

    // ================= GET BY ID =================
    @Override
    public ApiResponseDTO<RentClearanceStatusResponseDto> getById(Long id) {

        RentClearanceStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rent clearance status not found with id: " + id
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET BY CODE =================
    @Override
    public ApiResponseDTO<RentClearanceStatusResponseDto> getByCode(String code) {

        RentClearanceStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rent clearance status not found with code: " + code
                        )
                );

        return ApiResponseDTO.success(mapper.toResponseDto(entity));
    }

    // ================= GET ALL =================
    @Override
    public ApiResponseDTO<List<RentClearanceStatusResponseDto>> getAll() {

        List<RentClearanceStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(RentClearanceStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public ApiResponseDTO<List<RentClearanceStatusResponseDto>> getAllActive() {

        List<RentClearanceStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(RentClearanceStatusMaster::getDisplayOrder));

        return ApiResponseDTO.success(mapper.toResponseDtoList(list));
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public ApiResponseDTO<String> delete(Long id) {

        RentClearanceStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Rent clearance status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
        return ApiResponseDTO.success("Rent clearance status deleted successfully");
    }
}