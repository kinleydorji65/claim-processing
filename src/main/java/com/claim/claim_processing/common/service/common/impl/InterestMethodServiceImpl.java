package com.claim.claim_processing.common.service.common.impl;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;
import com.claim.claim_processing.common.entities.common.InterestMethodMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.InterestMethodMapper;
import com.claim.claim_processing.common.repository.common.InterestMethodRepository;
import com.claim.claim_processing.common.service.common.InterestMethodService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterestMethodServiceImpl implements InterestMethodService {

    private final InterestMethodRepository repository;
    private final InterestMethodMapper mapper;

    @Override
    public InterestMethodResponseDto create(InterestMethodRequestDto dto) {

        if (repository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException(
                    "Interest Method already exists with code: " + dto.getCode()
            );
        }

        InterestMethodMaster entity = mapper.toEntity(dto);

        entity.setCreatedBy(dto.getCreatedBy());

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public InterestMethodResponseDto update(Long id, InterestMethodRequestDto dto) {

        InterestMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Interest Method not found with id: " + id
                        )
                );

        mapper.updateEntityFromDto(dto, entity);

        entity.setUpdatedBy(dto.getUpdatedBy());

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public InterestMethodResponseDto getById(Long id) {

        InterestMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Interest Method not found with id: " + id
                        )
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public InterestMethodResponseDto getByCode(String code) {

        InterestMethodMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Interest Method not found with code: " + code
                        )
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterestMethodResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterestMethodResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    @Override
    public void delete(Long id) {

        InterestMethodMaster entity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Interest Method not found with id: " + id
                        )
                );

        entity.setIsActive(ActivityEnum.N);

        repository.save(entity);
    }
}