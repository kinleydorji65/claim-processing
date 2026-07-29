package com.claim.claim_processing.integration.member.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.AgencyMemberDetail;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;

public interface MemberService {
    ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String nppfNumber);
    ApiResponseDTO<List<AgencyMemberDetail>> getMemberDetailByAgencyCode(String agencyCode);
}
