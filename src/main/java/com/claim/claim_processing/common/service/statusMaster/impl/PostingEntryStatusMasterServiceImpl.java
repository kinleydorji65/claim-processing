package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.PostingEntryStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.PostingEntryStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.PostingEntryStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.PostingEntryStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostingEntryStatusMasterServiceImpl implements PostingEntryStatusMasterService {

    private final PostingEntryStatusMasterRepository repository;
    private final PostingEntryStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public PostingEntryStatusResponseDto create(PostingEntryStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Posting entry status code already exists: " + dto.getCode()
            );
        }

        PostingEntryStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        PostingEntryStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public PostingEntryStatusResponseDto update(Long id, PostingEntryStatusRequestDto dto) {

        PostingEntryStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting entry status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Posting entry status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        PostingEntryStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public PostingEntryStatusResponseDto getById(Long id) {

        PostingEntryStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting entry status not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public PostingEntryStatusResponseDto getByCode(String code) {

        PostingEntryStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting entry status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<PostingEntryStatusResponseDto> getAll() {

        List<PostingEntryStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(PostingEntryStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<PostingEntryStatusResponseDto> getAllActive() {

        List<PostingEntryStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(PostingEntryStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        PostingEntryStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting entry status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}