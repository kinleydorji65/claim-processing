package com.claim.claim_processing.common.service.impl.claim;

import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.mapper.claim.AccountTypeMapper;
import com.claim.claim_processing.common.repository.claim.AccountTypeRepository;
import com.claim.claim_processing.common.service.claim.AccountTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeMapper accountTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AccountTypeResponseDto> getAllActive() {
        List<AccountTypeMaster> accountTypes =
                accountTypeRepository.findByIsActiveOrderByNameAsc("Y");

        return accountTypeMapper.toResponseDtoList(accountTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTypeResponseDto getById(Long id) {
        AccountTypeMaster accountType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + id));

        return accountTypeMapper.toResponseDto(accountType);
    }

    @Override
    public AccountTypeResponseDto create(AccountTypeCreateRequestDto requestDto) {
        if (accountTypeRepository.existsByCode(requestDto.getCode())) {
            throw new RuntimeException("Account type code already exists: " + requestDto.getCode());
        }

        AccountTypeMaster accountType = accountTypeMapper.toEntity(requestDto);
        accountType.setCreatedBy("SYSTEM");

        AccountTypeMaster savedAccountType = accountTypeRepository.save(accountType);
        return accountTypeMapper.toResponseDto(savedAccountType);
    }

    @Override
    public AccountTypeResponseDto update(Long id, AccountTypeUpdateRequestDto requestDto) {
        AccountTypeMaster existingAccountType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + id));

        accountTypeMapper.updateEntityFromDto(requestDto, existingAccountType);
        existingAccountType.setUpdatedBy("SYSTEM");

        AccountTypeMaster updatedAccountType = accountTypeRepository.save(existingAccountType);
        return accountTypeMapper.toResponseDto(updatedAccountType);
    }

    @Override
    public void deactivate(Long id) {
        AccountTypeMaster existingAccountType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + id));

        existingAccountType.setIsActive("N");
        existingAccountType.setUpdatedBy("SYSTEM");

        accountTypeRepository.save(existingAccountType);
    }
}
