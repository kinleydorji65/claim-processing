package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ActionMasterMapper;
import com.claim.claim_processing.common.repository.common.ActionMasterRepository;
import com.claim.claim_processing.common.service.common.ActionMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionMasterServiceImpl implements ActionMasterService {

    private final ActionMasterRepository repository;
    private final ActionMasterMapper mapper;

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public ActionResponseDto create(ActionRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Code already exists: " + dto.getCode());
        }

        ActionMaster entity = mapper.toEntity(dto);
        entity.setIsActive(ActivityEnum.Y);

        return mapper.toDto(repository.save(entity));
    }

    // -------------------------------
    // PATCH (PARTIAL UPDATE)
    // -------------------------------
    @Override
    public ActionResponseDto patch(ActionRequestDto dto) {

        ActionMaster entity = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Action not found: " + dto.getId()));

        mapper.patchEntityFromDto(dto, entity);

        return mapper.toDto(repository.save(entity));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public ActionResponseDto getById(Long id) {

        ActionMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action not found: " + id));

        return mapper.toDto(entity);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ActionResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ActionResponseDto> getAllActive() {

        return repository.findByIsActive(ActivityEnum.Y)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // -------------------------------
    // SOFT DELETE
    // -------------------------------
    @Override
    public void delete(Long id) {

        ActionMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action not found: " + id));

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}