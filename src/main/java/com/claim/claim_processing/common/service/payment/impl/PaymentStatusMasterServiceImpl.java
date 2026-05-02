package com.claim.claim_processing.common.service.payment.impl;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentStatusMaster;
import com.claim.claim_processing.common.mapper.payment.PaymentStatusMasterMapper;
import com.claim.claim_processing.common.repository.payment.PaymentStatusMasterRepository;
import com.claim.claim_processing.common.service.payment.PaymentStatusMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStatusMasterServiceImpl implements PaymentStatusMasterService {

    private final PaymentStatusMasterRepository repository;
    private final PaymentStatusMasterMapper mapper;

    @Override
    public PaymentStatusResponseDto create(PaymentStatusRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Payment status code already exists.");
        }

        if (repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Payment status name already exists.");
        }

        PaymentStatusMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public PaymentStatusResponseDto update(Long id,
                                           PaymentStatusRequestDto requestDto) {

        PaymentStatusMaster entity = getEntityById(id);

        if (!entity.getCode().equals(requestDto.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Payment status code already exists.");
        }

        if (!entity.getName().equals(requestDto.getName())
                && repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Payment status name already exists.");
        }

        mapper.updateEntityFromDto(requestDto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponseDto getById(Long id) {
        return mapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponseDto getByCode(String code) {

        PaymentStatusMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("Payment status not found with code: " + code));

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatusResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatusResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    @Override
    public void delete(Long id) {

        PaymentStatusMaster entity = getEntityById(id);

        if (entity.getIsActive() == ActivityEnum.N) {
            throw ClaimException.conflict("Payment status already deleted.");
        }

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);
    }

    private PaymentStatusMaster getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("Payment status not found with id: " + id));
    }
}