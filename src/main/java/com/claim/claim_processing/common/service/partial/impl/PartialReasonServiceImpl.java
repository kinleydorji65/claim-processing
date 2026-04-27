package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.mapper.partial.PartialReasonMapper;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.service.partial.PartialReasonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialReasonServiceImpl implements PartialReasonService {

    private static final String NOT_FOUND = "Partial withdrawal reason not found with id: ";

    private final PartialReasonRepository repository;
    private final PartialReasonMapper mapper;

    // ================= CREATE =================

    @Override
    public PartialWithdrawalReasonResponseDto create(PartialWithdrawalReasonRequestDto requestDto) {

        PartialWithdrawalReasonMaster entity = mapper.toEntity(requestDto);

        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        try {
            PartialWithdrawalReasonMaster saved = repository.save(entity);
            return mapper.toResponseDto(saved);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Partial reason code already exists: " + requestDto.getCode()
            );
        }
    }

    // ================= GET BY ID =================

    @Override
    public PartialWithdrawalReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    // ================= GET BY CODE =================

    @Override
    public PartialWithdrawalReasonResponseDto getByCode(String code) {

        PartialWithdrawalReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        new EntityNotFoundException("Partial reason not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    // ================= GET ALL =================

    @Override
    public List<PartialWithdrawalReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // ================= GET ACTIVE =================

    @Override
    public List<PartialWithdrawalReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y)
        );
    }

    // ================= UPDATE =================

    @Override
    public PartialWithdrawalReasonResponseDto update(Long id,
                                                     PartialWithdrawalReasonUpdateDto updateDto) {

        PartialWithdrawalReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        PartialWithdrawalReasonMaster updated = repository.save(entity);

        return mapper.toResponseDto(updated);
    }

    // ================= SOFT DELETE =================

    @Override
    public void deactivate(Long id) {

        PartialWithdrawalReasonMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    // ================= COMMON METHOD =================

    private PartialWithdrawalReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}