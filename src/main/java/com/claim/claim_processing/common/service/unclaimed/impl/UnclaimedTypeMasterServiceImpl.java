package com.claim.claim_processing.common.service.unclaimed.impl;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedTypeMaster;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedTypeMasterService;
import com.claim.claim_processing.common.mapper.unclaimed.UnclaimedTypeMasterMapper;
import com.claim.claim_processing.common.repository.unclaimed.UnclaimedTypeMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnclaimedTypeMasterServiceImpl
        implements UnclaimedTypeMasterService {

    private final UnclaimedTypeMasterRepository repository;
    private final UnclaimedTypeMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public UnclaimedTypeResponseDto create(UnclaimedTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Unclaimed type code already exists: " + dto.getCode()
            );
        }

        UnclaimedTypeMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        UnclaimedTypeMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public UnclaimedTypeResponseDto update(Long id, UnclaimedTypeRequestDto dto) {

        UnclaimedTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed type not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Unclaimed type code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        UnclaimedTypeMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public UnclaimedTypeResponseDto getById(Long id) {

        UnclaimedTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed type not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public UnclaimedTypeResponseDto getByCode(String code) {

        UnclaimedTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed type not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<UnclaimedTypeResponseDto> getAll() {

        List<UnclaimedTypeMaster> list = repository.findAll();

        list.sort(Comparator.comparing(UnclaimedTypeMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<UnclaimedTypeResponseDto> getAllActive() {

        List<UnclaimedTypeMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(UnclaimedTypeMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        UnclaimedTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed type not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}