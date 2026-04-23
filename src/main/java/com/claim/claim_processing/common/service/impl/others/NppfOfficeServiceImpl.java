package com.claim.claim_processing.common.service.impl.others;

import com.claim.claim_processing.common.DTO.request.others.NppfOfficeRequestDto;
import com.claim.claim_processing.common.DTO.response.others.NppfOfficeResponseDto;
import com.claim.claim_processing.common.DTO.update.others.NppfOfficeUpdateDto;
import com.claim.claim_processing.common.entities.others.NppfOfficeMaster;
import com.claim.claim_processing.common.mapper.others.NppfOfficeMapper;
import com.claim.claim_processing.common.repository.others.NppfOfficeRepository;
import com.claim.claim_processing.common.service.others.NppfOfficeService;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NppfOfficeServiceImpl implements NppfOfficeService {

    private static final String ACTIVE = "Y";
    private static final String INACTIVE = "N";

    private final NppfOfficeRepository repository;
    private final NppfOfficeMapper mapper;

    @Override
    public NppfOfficeResponseDto create(NppfOfficeRequestDto requestDto) {
        if (repository.existsByCode(requestDto.getCode())) {
            throw ClaimException.conflict(
                    "NPPF office code already exists: " + requestDto.getCode()
            );
        }

        NppfOfficeMaster entity = mapper.toEntity(requestDto);
        entity.setCreatedBy("SYSTEM");
        entity.setUpdatedBy("SYSTEM");

        NppfOfficeMaster saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Override
    public NppfOfficeResponseDto getById(Long id) {
        return mapper.toResponseDto(findById(id));
    }

    @Override
    public NppfOfficeResponseDto getByCode(Long code) {
        NppfOfficeMaster entity = repository.findByCode(code)
                .orElseThrow(() ->
                        ClaimException.notFound("NPPF office not found with code: " + code)
                );

        return mapper.toResponseDto(entity);
    }

    @Override
    public List<NppfOfficeResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public List<NppfOfficeResponseDto> getAllActive() {
        return mapper.toResponseDtoList(repository.findByIsActiveOrderByNameAsc(ACTIVE));
    }

    @Override
    public NppfOfficeResponseDto update(Long id, NppfOfficeUpdateDto updateDto) {
        NppfOfficeMaster entity = findById(id);

        mapper.updateEntityFromDto(updateDto, entity);
        entity.setUpdatedBy("SYSTEM");

        NppfOfficeMaster updated = repository.save(entity);
        return mapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        NppfOfficeMaster entity = findById(id);

        entity.setIsActive(INACTIVE);
        entity.setUpdatedBy("SYSTEM");

        repository.save(entity);
    }

    private NppfOfficeMaster findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        ClaimException.notFound("NPPF office not found with id: " + id)
                );
    }
}