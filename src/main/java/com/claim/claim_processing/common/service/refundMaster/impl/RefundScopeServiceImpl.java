package com.claim.claim_processing.common.service.refundMaster.impl;

import com.claim.claim_processing.common.DTO.request.refund_master.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.RefundScopeUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refundMaster.RefundScopeMaster;
import com.claim.claim_processing.common.mapper.refund_master.RefundScopeMapper;
import com.claim.claim_processing.common.repository.refund_master.RefundScopeRepository;
import com.claim.claim_processing.common.service.refundMaster.RefundScopeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundScopeServiceImpl implements RefundScopeService {

    private final RefundScopeRepository repository;
    private final RefundScopeMapper mapper;

    @Override
    public RefundScopeResponseDto create(RefundScopeRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Refund scope code already exists: " + requestDto.getCode()
            );
        }

        RefundScopeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        RefundScopeMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public RefundScopeResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public RefundScopeResponseDto getByCode(String code) {
        RefundScopeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Refund scope not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<RefundScopeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<RefundScopeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActive(ActivityEnum.Y));
    }

    @Override
    public RefundScopeResponseDto update(Long id, RefundScopeUpdateDto updateDto) {
        RefundScopeMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        RefundScopeMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        RefundScopeMaster entity = findById(id);
        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");
        repository.save(entity);
    }

    private RefundScopeMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Refund scope not found with id: " + id)
                );
    }
}