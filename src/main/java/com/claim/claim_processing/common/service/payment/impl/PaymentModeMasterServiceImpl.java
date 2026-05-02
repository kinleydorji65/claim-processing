package com.claim.claim_processing.common.service.payment.impl;

import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentModeMaster;
import com.claim.claim_processing.common.mapper.payment.PaymentModeMasterMapper;
import com.claim.claim_processing.common.repository.payment.PaymentModeMasterRepository;
import com.claim.claim_processing.common.service.payment.PaymentModeMasterService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentModeMasterServiceImpl implements PaymentModeMasterService {

    private final PaymentModeMasterRepository repository;
    private final PaymentModeMasterMapper mapper;

    @Override
    public PaymentModeResponseDto create(PaymentModeRequestDto requestDto) {

        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Payment mode code already exists.");
        }

        if (repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Payment mode name already exists.");
        }

        PaymentModeMaster entity = mapper.toEntity(requestDto);

        if (entity.getIsActive() == null) {
            entity.setIsActive(ActivityEnum.Y);
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public PaymentModeResponseDto update(Long id,
                                         PaymentModeRequestDto requestDto) {

        PaymentModeMaster entity = getEntityById(id);

        if (!entity.getCode().equals(requestDto.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Payment mode code already exists.");
        }

        if (!entity.getName().equals(requestDto.getName())
                && repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Payment mode name already exists.");
        }

        mapper.updateEntityFromDto(requestDto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public PaymentModeResponseDto patch(Long id,
                                        PaymentModeRequestDto requestDto) {

        PaymentModeMaster entity = getEntityById(id);

        if (requestDto.getCode() != null
                && !entity.getCode().equals(requestDto.getCode())
                && repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict("Payment mode code already exists.");
        }

        if (requestDto.getName() != null
                && !entity.getName().equals(requestDto.getName())
                && repository.existsByName(requestDto.getName())) {
            throw ClaimException.conflict("Payment mode name already exists.");
        }

        mapper.patchEntityFromDto(requestDto, entity);

        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentModeResponseDto getById(Long id) {
        return mapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentModeResponseDto getByCode(String code) {

        PaymentModeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment mode not found with code: " + code
                        ));

        return mapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentModeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentModeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(
                repository.findByIsActive(ActivityEnum.Y)
        );
    }

    @Override
    public void delete(Long id) {

        PaymentModeMaster entity = getEntityById(id);

        if (entity.getIsActive() == ActivityEnum.N) {
            throw ClaimException.conflict("Payment mode already deleted.");
        }

        entity.setIsActive(ActivityEnum.N);
        repository.save(entity);
    }

    private PaymentModeMaster getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound(
                                "Payment mode not found with id: " + id
                        ));
    }
}