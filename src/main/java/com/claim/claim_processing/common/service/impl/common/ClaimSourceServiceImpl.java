package com.claim.claim_processing.common.service.impl.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;
import com.claim.claim_processing.common.entities.common.ClaimSourceMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.mapper.common.ClaimSourceMapper;
import com.claim.claim_processing.common.repository.common.ClaimSourceRepository;
import com.claim.claim_processing.common.service.common.ClaimSourceService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimSourceServiceImpl implements ClaimSourceService {


    private final ClaimSourceRepository repository;
    private final ClaimSourceMapper mapper;

    @Override
    public ClaimSourceResponseDto create(ClaimSourceRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Claim source code already exists: " + requestDto.getCode());
        }

        ClaimSourceMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        ClaimSourceMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public ClaimSourceResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public ClaimSourceResponseDto getByCode(String code) {
        ClaimSourceMaster entity = repository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound("Claim source not found with code: " + code));

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<ClaimSourceResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<ClaimSourceResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ActivityEnum.Y));
    }

    @Override
    public ClaimSourceResponseDto update(Long id, ClaimSourceUpdateDto updateDto) {
        ClaimSourceMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        ClaimSourceMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void deactivate(Long id) {
        ClaimSourceMaster entity = findById(id);
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");
        repository.save(entity);
    }

    private ClaimSourceMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ClaimException.notFound("Claim source not found with id: " + id));
    }
}