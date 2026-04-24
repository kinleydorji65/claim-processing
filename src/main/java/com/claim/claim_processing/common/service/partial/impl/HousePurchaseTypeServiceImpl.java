package com.claim.claim_processing.common.service.partial.impl;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.HousePurchaseTypeMaster;
import com.claim.claim_processing.common.mapper.partial.HousePurchaseTypeMapper;
import com.claim.claim_processing.common.repository.partial.HousePurchaseTypeRepository;
import com.claim.claim_processing.common.service.partial.HousePurchaseTypeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HousePurchaseTypeServiceImpl implements HousePurchaseTypeService {


    private final HousePurchaseTypeRepository repository;
    private final HousePurchaseTypeMapper mapper;

    @Override
    public HousePurchaseTypeResponseDto create(HousePurchaseTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "House purchase type code already exists: " + requestDto.getCode()
            );
        }

        HousePurchaseTypeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        HousePurchaseTypeMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public HousePurchaseTypeResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public HousePurchaseTypeResponseDto getByCode(String code) {
        HousePurchaseTypeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("House purchase type not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<HousePurchaseTypeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<HousePurchaseTypeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
        );
    }

    @Override
    public HousePurchaseTypeResponseDto update(Long id, HousePurchaseTypeUpdateDto updateDto) {

        HousePurchaseTypeMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        HousePurchaseTypeMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {

        HousePurchaseTypeMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private HousePurchaseTypeMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("House purchase type not found with id: " + id)
                );
    }
}