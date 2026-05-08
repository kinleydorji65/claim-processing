package com.claim.claim_processing.common.service.unclaimed.impl;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedStatusMaster;
import com.claim.claim_processing.common.mapper.unclaimed.UnclaimedStatusMasterMapper;
import com.claim.claim_processing.common.repository.unclaimed.UnclaimedStatusMasterRepository;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnclaimedStatusMasterServiceImpl
        implements UnclaimedStatusMasterService {

    private final UnclaimedStatusMasterRepository repository;
    private final UnclaimedStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public UnclaimedStatusResponseDto create(UnclaimedStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Unclaimed status code already exists: " + dto.getCode()
            );
        }

        UnclaimedStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        if (entity.getDisplayOrder() == null) {
            entity.setDisplayOrder(1);
        }

        UnclaimedStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public UnclaimedStatusResponseDto update(Long id, UnclaimedStatusRequestDto dto) {

        UnclaimedStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Unclaimed status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        UnclaimedStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public UnclaimedStatusResponseDto getById(Long id) {

        UnclaimedStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed status not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public UnclaimedStatusResponseDto getByCode(String code) {

        UnclaimedStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<UnclaimedStatusResponseDto> getAll() {

        List<UnclaimedStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(UnclaimedStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<UnclaimedStatusResponseDto> getAllActive() {

        List<UnclaimedStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(UnclaimedStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        UnclaimedStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}