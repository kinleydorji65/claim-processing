package com.claim.claim_processing.common.service.contribution.impl;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.mapper.contribution.BenefitComponentTypeMasterMapper;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeMasterRepository;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeMasterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitComponentTypeMasterServiceImpl
        implements BenefitComponentTypeMasterService {

    private final BenefitComponentTypeMasterRepository repository;
    private final BenefitComponentTypeMasterMapper mapper;

    @Override
    public BenefitComponentTypeMasterResponseDto create(
            BenefitComponentTypeMasterRequestDto requestDto
    ) {
        validateDuplicateCode(requestDto.getCode());

        BenefitComponentTypeMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        BenefitComponentTypeMaster saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public BenefitComponentTypeMasterResponseDto update(
            Long id,
            BenefitComponentTypeMasterRequestDto requestDto
    ) {
        BenefitComponentTypeMaster entity = findEntityById(id);

        if (requestDto.getCode() != null &&
                !requestDto.getCode().equalsIgnoreCase(entity.getCode())) {

            validateDuplicateCode(requestDto.getCode());
        }

        mapper.updateEntityFromDto(requestDto, entity);

        BenefitComponentTypeMaster updated = repository.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BenefitComponentTypeMasterResponseDto getById(Long id) {
        return mapper.toDto(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentTypeMasterResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentTypeMasterResponseDto> getByStatus(
            ActivityEnum isActive
    ) {
        return repository.findByIsActive(isActive)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitComponentTypeMasterResponseDto> searchByName(
            String keyword
    ) {
        return repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        BenefitComponentTypeMaster entity = findEntityById(id);
        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);
    }

    // =========================
    // PRIVATE METHODS
    // =========================

    private BenefitComponentTypeMaster findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Benefit Component Type not found with id: " + id
                        ));
    }

    private void validateDuplicateCode(String code) {
        if (code != null && repository.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "Code already exists: " + code
            );
        }
    }
}