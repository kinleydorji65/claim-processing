package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

import java.util.List;

public interface ClaimApplicationService {

    ClaimApplication create(ClaimApplicationRequestDto request);

    ClaimApplication update(Long id, ClaimApplicationRequestDto request);

    ClaimApplication getById(Long id);

    ClaimApplication getByApplicationNumber(String applicationNumber);

    List<ClaimApplication> getAll();

    List<ClaimApplication> getByMemberCode(String memberCode);

    List<ClaimApplication> getByNppfNumber(String nppfNumber);
}