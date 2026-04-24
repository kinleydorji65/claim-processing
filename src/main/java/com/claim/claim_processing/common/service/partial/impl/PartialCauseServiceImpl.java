package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.mapper.partial.PartialCauseMapper;
import com.claim.claim_processing.common.repository.partial.PartialCauseRepository;
import com.claim.claim_processing.common.service.partial.PartialCauseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialCauseServiceImpl implements PartialCauseService {
    private static final String NOT_FOUND_MESSAGE = "Partial withdrawal cause not found with id: ";

    private final PartialCauseRepository repository;
    private final PartialCauseMapper mapper;

    @Override
    public PartialCauseResponseDto create(PartialCauseRequestDto requestDto) {
        validateDuplicateCode(requestDto.getCode());

        PartialWithdrawalCauseMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        PartialWithdrawalCauseMaster savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }

    @Override
    public PartialCauseResponseDto getById(Long id) {
        PartialWithdrawalCauseMaster entity = findEntityById(id);
        return mapper.toResponseDto(entity);
    }

    @Override
    public PartialCauseResponseDto getByCode(String code) {
        PartialWithdrawalCauseMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Partial withdrawal cause not found with code: " + code));
        return mapper.toResponseDto(entity);
    }

    @Override
    public List<PartialCauseResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<PartialCauseResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y));
    }

    @Override
    public PartialCauseResponseDto update(Long id, PartialCauseUpdateDto updateDto) {
        PartialWithdrawalCauseMaster entity = findEntityById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        PartialWithdrawalCauseMaster updatedEntity = repository.save(entity);
        return mapper.toResponseDto(updatedEntity);
    }

    @Override
    public void delete(Long id) {
        PartialWithdrawalCauseMaster entity = findEntityById(id);
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");
        repository.save(entity);
    }

    private PartialWithdrawalCauseMaster findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    private void validateDuplicateCode(String code) {
        if (repository.existsByCode(code)) {
            throw new IllegalArgumentException("Partial withdrawal cause code already exists: " + code);
        }
    }
}