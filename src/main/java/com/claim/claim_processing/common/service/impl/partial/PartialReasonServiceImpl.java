package com.claim.claim_processing.common.service.impl.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import com.claim.claim_processing.common.mapper.partial.PartialReasonMapper;
import com.claim.claim_processing.common.repository.partial.PartialReasonRepository;
import com.claim.claim_processing.common.service.partial.PartialReasonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartialReasonServiceImpl implements PartialReasonService {

    private static final String ACTIVE = "Y";
    private static final String INACTIVE = "N";
    private static final String NOT_FOUND = "Partial withdrawal reason not found with id: ";

    private final PartialReasonRepository repository;
    private final PartialReasonMapper mapper;

    @Override
    public PartialReasonResponseDto create(PartialReasonRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw new IllegalArgumentException("Partial reason code already exists: " + requestDto.getCode());
        }

        PartialWithdrawalReasonMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        PartialWithdrawalReasonMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public PartialReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public PartialReasonResponseDto getByCode(String code) {
        PartialWithdrawalReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Partial reason not found with code: " + code));
        return mapper.toResponseDto(entity);
    }

    @Override
    public List<PartialReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<PartialReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ACTIVE));
    }

    @Override
    public PartialReasonResponseDto update(Long id, PartialReasonUpdateDto updateDto) {

        PartialWithdrawalReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        PartialWithdrawalReasonMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {

        PartialWithdrawalReasonMaster entity = findById(id);

        // Soft delete
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    // 🔹 Common reusable method
    private PartialWithdrawalReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}