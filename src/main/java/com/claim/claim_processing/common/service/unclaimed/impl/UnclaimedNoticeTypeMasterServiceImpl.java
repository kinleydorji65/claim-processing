package com.claim.claim_processing.common.service.unclaimed.impl;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedNoticeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedNoticeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedNoticeTypeMaster;
import com.claim.claim_processing.common.mapper.unclaimed.UnclaimedNoticeTypeMasterMapper;
import com.claim.claim_processing.common.repository.unclaimed.UnclaimedNoticeTypeMasterRepository;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedNoticeTypeMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnclaimedNoticeTypeMasterServiceImpl
        implements UnclaimedNoticeTypeMasterService {

    private final UnclaimedNoticeTypeMasterRepository repository;
    private final UnclaimedNoticeTypeMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public UnclaimedNoticeTypeResponseDto create(UnclaimedNoticeTypeRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Unclaimed notice type code already exists: " + dto.getCode()
            );
        }

        UnclaimedNoticeTypeMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        UnclaimedNoticeTypeMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public UnclaimedNoticeTypeResponseDto update(Long id, UnclaimedNoticeTypeRequestDto dto) {

        UnclaimedNoticeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed notice type not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Unclaimed notice type code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        UnclaimedNoticeTypeMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public UnclaimedNoticeTypeResponseDto getById(Long id) {

        UnclaimedNoticeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed notice type not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public UnclaimedNoticeTypeResponseDto getByCode(String code) {

        UnclaimedNoticeTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed notice type not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<UnclaimedNoticeTypeResponseDto> getAll() {

        List<UnclaimedNoticeTypeMaster> list = repository.findAll();

        list.sort(Comparator.comparing(UnclaimedNoticeTypeMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<UnclaimedNoticeTypeResponseDto> getAllActive() {

        List<UnclaimedNoticeTypeMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(UnclaimedNoticeTypeMaster::getId));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        UnclaimedNoticeTypeMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Unclaimed notice type not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}