package com.claim.claim_processing.common.service.refundMaster.impl;

import com.claim.claim_processing.common.DTO.request.refund_master.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.refund_master.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refund_master.ExcessRefundReasonMaster;
import com.claim.claim_processing.common.mapper.refund_master.ExcessRefundReasonMapper;
import com.claim.claim_processing.common.repository.refund_master.ExcessRefundReasonRepository;
import com.claim.claim_processing.common.service.refundMaster.ExcessRefundReasonService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcessRefundReasonServiceImpl implements ExcessRefundReasonService {

    private final ExcessRefundReasonRepository repository;
    private final ExcessRefundReasonMapper mapper;

    @Override
    public ExcessRefundReasonResponseDto create(ExcessRefundReasonRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "Excess refund reason code already exists: " + requestDto.getCode()
            );
        }

        ExcessRefundReasonMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        ExcessRefundReasonMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public ExcessRefundReasonResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public ExcessRefundReasonResponseDto getByCode(String code) {
        ExcessRefundReasonMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Excess refund reason not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<ExcessRefundReasonResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<ExcessRefundReasonResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum.Y)
        );
    }

    @Override
    public ExcessRefundReasonResponseDto update(Long id, ExcessRefundReasonUpdateDto updateDto) {

        ExcessRefundReasonMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        ExcessRefundReasonMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {

        ExcessRefundReasonMaster entity = findById(id);

        entity.setIsActive(ActivityEnum.N);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private ExcessRefundReasonMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Excess refund reason not found with id: " + id)
                );
    }
}