package com.claim.claim_processing.common.service.claim.impl;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import com.claim.claim_processing.common.mapper.claim.VestingRefundTypeMapper;
import com.claim.claim_processing.common.repository.claim.VestingRefundTypeRepository;
import com.claim.claim_processing.common.service.claim.VestingRefundTypeService;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VestingRefundTypeServiceImpl implements VestingRefundTypeService {

    private final VestingRefundTypeRepository repository;
    private final VestingRefundTypeMapper mapper;

    @Override
    public VestingRefundTypeResponseDto create(VestingRefundTypeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "VestingRefundType already exists with code: " + requestDto.getCode()
            );
        }

        VestingRefundType entity = mapper.toEntity(requestDto);

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public VestingRefundTypeResponseDto update(Long id, VestingRefundTypeRequestDto requestDto) {

        VestingRefundType existing = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );

        if (requestDto.getCode() != null
                && !requestDto.getCode().equals(existing.getCode())
                && repository.existsByCode(requestDto.getCode())) {

            throw ClaimException.conflict(
                    "VestingRefundType already exists with code: " + requestDto.getCode()
            );
        }

        mapper.updateEntityFromDto(requestDto, existing);

        return mapper.toDto(repository.save(existing));
    }

    @Override
    public VestingRefundTypeResponseDto getById(Long id) {

        VestingRefundType entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );

        return mapper.toDto(entity);
    }

    @Override
    public List<VestingRefundTypeResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {

        VestingRefundType entity = repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("VestingRefundType not found: " + id)
                );

        repository.delete(entity);
    }
}