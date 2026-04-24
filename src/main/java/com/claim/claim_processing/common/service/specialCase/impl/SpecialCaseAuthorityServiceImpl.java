package com.claim.claim_processing.common.service.specialCase.impl;

import com.claim.claim_processing.common.DTO.request.special_case.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.special_case.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import com.claim.claim_processing.common.mapper.special_case.SpecialCaseAuthorityMapper;
import com.claim.claim_processing.common.repository.special_case.SpecialCaseAuthorityRepository;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseAuthorityService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialCaseAuthorityServiceImpl implements SpecialCaseAuthorityService {

    private static final String NOT_FOUND_MESSAGE = "Special case authority not found with id: ";

    private final SpecialCaseAuthorityRepository repository;
    private final SpecialCaseAuthorityMapper mapper;

    @Override
    public SpecialCaseAuthorityResponseDto create(SpecialCaseAuthorityRequestDto requestDto) {
        validateDuplicateCode(requestDto.getCode());

        SpecialCaseRefundAuthorityMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        SpecialCaseRefundAuthorityMaster savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }

    @Override
    public SpecialCaseAuthorityResponseDto getById(Long id) {
        SpecialCaseRefundAuthorityMaster entity = findEntityById(id);
        return mapper.toResponseDto(entity);
    }

    @Override
    public SpecialCaseAuthorityResponseDto getByCode(String code) {
        SpecialCaseRefundAuthorityMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Special case authority not found with code: " + code));
        return mapper.toResponseDto(entity);
    }

    @Override
    public List<SpecialCaseAuthorityResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<SpecialCaseAuthorityResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y));
    }

    @Override
    public SpecialCaseAuthorityResponseDto update(Long id, SpecialCaseAuthorityUpdateRequestDto updateRequestDto) {
        SpecialCaseRefundAuthorityMaster entity = findEntityById(id);

        mapper.updateEntityFromDto(updateRequestDto, entity);
        entity.setUpdatedBy("SYSTEM");

        SpecialCaseRefundAuthorityMaster updatedEntity = repository.save(entity);
        return mapper.toResponseDto(updatedEntity);
    }

    @Override
    public void delete(Long id) {
        SpecialCaseRefundAuthorityMaster entity = findEntityById(id);
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");
        repository.save(entity);
    }

    private SpecialCaseRefundAuthorityMaster findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    private void validateDuplicateCode(String code) {
        if (repository.existsByCode(code)) {
            throw new IllegalArgumentException("Special case authority code already exists: " + code);
        }
    }
}