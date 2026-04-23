package com.claim.claim_processing.common.service.impl.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.DisasterTypeMaster;
import com.claim.claim_processing.common.mapper.partial.DisasterTypeMapper;
import com.claim.claim_processing.common.repository.partial.DisasterTypeRepository;
import com.claim.claim_processing.common.service.partial.DisasterTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisasterTypeServiceImpl implements DisasterTypeService {

    private static final String ACTIVE = "Y";
    private static final String INACTIVE = "N";
    private static final String NOT_FOUND = "Disaster type not found with id: ";

    private final DisasterTypeRepository repository;
    private final DisasterTypeMapper mapper;

    @Override
    public DisasterTypeResponseDto create(DisasterTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw new IllegalArgumentException("Disaster type code already exists: " + requestDto.getCode());
        }

        DisasterTypeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        DisasterTypeMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public DisasterTypeResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public DisasterTypeResponseDto getByCode(String code) {
        DisasterTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Disaster type not found with code: " + code));
        return mapper.toResponseDto(entity);
    }

    @Override
    public List<DisasterTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<DisasterTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ACTIVE));
    }

    @Override
    public DisasterTypeResponseDto update(Long id, DisasterTypeUpdateDto updateDto) {

        DisasterTypeMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        DisasterTypeMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {

        DisasterTypeMaster entity = findById(id);

        // Soft delete
        entity.setIsActive(ActivityEnum.Y);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    //  Common reusable method
    private DisasterTypeMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}