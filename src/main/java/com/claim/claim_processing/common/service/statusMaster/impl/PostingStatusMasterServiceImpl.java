package com.claim.claim_processing.common.service.statusMaster.impl;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.PostingStatusMaster;
import com.claim.claim_processing.common.mapper.statusMaster.PostingStatusMasterMapper;
import com.claim.claim_processing.common.repository.statusMaster.PostingStatusMasterRepository;
import com.claim.claim_processing.common.service.statusMaster.PostingStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostingStatusMasterServiceImpl implements PostingStatusMasterService {

    private final PostingStatusMasterRepository repository;
    private final PostingStatusMasterMapper mapper;

    // ================= CREATE =================
    @Override
    public PostingStatusResponseDto create(PostingStatusRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw ClaimException.conflict(
                    "Posting status code already exists: " + dto.getCode()
            );
        }

        PostingStatusMaster entity = mapper.toEntity(dto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        PostingStatusMaster saved = repository.save(entity);

        return mapper.toResponseDto(saved);
    }

    // ================= UPDATE =================
    @Override
    public PostingStatusResponseDto update(Long id, PostingStatusRequestDto dto) {

        PostingStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting status not found with id: " + id
                        )
                );

        if (dto.getCode() != null &&
                !dto.getCode().equals(entity.getCode()) &&
                repository.existsByCode(dto.getCode())) {

            throw ClaimException.conflict(
                    "Posting status code already exists: " + dto.getCode()
            );
        }

        mapper.updateEntity(entity, dto);

        PostingStatusMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= GET BY ID =================
    @Override
    public PostingStatusResponseDto getById(Long id) {

        PostingStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting status not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET BY CODE =================
    @Override
    public PostingStatusResponseDto getByCode(String code) {

        PostingStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting status not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================
    @Override
    public List<PostingStatusResponseDto> getAll() {

        List<PostingStatusMaster> list = repository.findAll();

        list.sort(Comparator.comparing(PostingStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= GET ALL ACTIVE =================
    @Override
    public List<PostingStatusResponseDto> getAllActive() {

        List<PostingStatusMaster> list =
                repository.findByIsActive(ActivityEnum.Y);

        list.sort(Comparator.comparing(PostingStatusMaster::getDisplayOrder));

        return mapper.toResponseDtoList(list);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Long id) {

        PostingStatusMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Posting status not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}