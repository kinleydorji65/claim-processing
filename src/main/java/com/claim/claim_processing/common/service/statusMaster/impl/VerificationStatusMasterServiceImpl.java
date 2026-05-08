package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.VerificationStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.VerificationStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.VerificationStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.VerificationStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationStatusMasterServiceImpl
        implements VerificationStatusMasterService {

    private final VerificationStatusMasterRepository repository;
    private final VerificationStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public VerificationStatusResponseDto create(VerificationStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Verification status code already exists: " + dto.getCode()
            );
        }

        VerificationStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        VerificationStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public VerificationStatusResponseDto update(Long id, VerificationStatusRequestDto dto) {

        VerificationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Verification status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Verification status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        VerificationStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public VerificationStatusResponseDto getById(Long id) {

        VerificationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Verification status not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public VerificationStatusResponseDto getByCode(String code) {

        VerificationStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Verification status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<VerificationStatusResponseDto> getAll() {

        List<VerificationStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(VerificationStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<VerificationStatusResponseDto> getAllActive() {

        List<VerificationStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(VerificationStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        VerificationStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Verification status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}