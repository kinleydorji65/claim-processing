package com.claim.claim_processing.integration.member.service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;

public interface MemberService {
    ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String memberCode, String agencyCode);
}
