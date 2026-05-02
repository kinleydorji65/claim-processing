package com.claim.claim_processing.integration.member.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.claim.claim_processing.common.DTO.response.others.member.MemberBankResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberFamilyResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberNomineeResponseDto;
import com.claim.claim_processing.common.entities.others.member.MemberBank;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.entities.others.member.MemberFamily;
import com.claim.claim_processing.common.entities.others.member.MemberNominee;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.EmploymentTypeRepository;
import com.claim.claim_processing.common.repository.others.PersonIdentityRepository;
import com.claim.claim_processing.common.repository.others.RelationTypeRepository;
import com.claim.claim_processing.exceptions.ClaimException;

@Mapper(componentModel = "spring")
public abstract class MemberDetailMapper {
    @Autowired
    protected PersonIdentityRepository personIdentityRepository;
    @Autowired
    protected EmploymentTypeRepository employmentTypeRepository;
    @Autowired
    protected BankTypeRepository bankTypeRepository;
    @Autowired
    protected RelationTypeRepository relationTypeRepository;

    @Mapping(target = "memberName", ignore = true) // We'll set this in @AfterMapping
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "memberStatus", source = "status")

    public abstract MemberDetailResponseDto toMemberDetailResponseDto(MemberDetail memberDetail);

    @AfterMapping
protected void setOtherDetails(MemberDetail memberDetail, @MappingTarget MemberDetailResponseDto responseDto) {
    responseDto.setMemberName(getFullName(memberDetail.getFirstName(), memberDetail.getMiddleName(), memberDetail.getLastName()));
    String identityTypeName = personIdentityRepository.findById(memberDetail.getIdentityTypeId()).orElseThrow(()-> ClaimException.notFound("Identy type not found with ID: " + memberDetail.getId())).getName();
    String employmentTypeName = employmentTypeRepository.findById(memberDetail.getWorkInfo().getEmploymentTypeId()).orElseThrow(()-> ClaimException.notFound("Employment type not found with ID: " + memberDetail.getId())).getEmploymentTypeName();
    responseDto.setDateOfServiceJoiningDate(memberDetail.getEffectiveFrom());
    responseDto.setIdentityTypeName(identityTypeName);
    responseDto.setEmploymentTypeName(employmentTypeName);
    responseDto.setMemberStatus(memberDetail.getStatus());
    responseDto.setBasicSalary(memberDetail.getWorkInfo().getBasicPay() != null ? memberDetail.getWorkInfo().getBasicPay().toString() : null);
    responseDto.setMemberBanks(toMemberBankResponseList(memberDetail.getMemberBanks()));
    responseDto.setMemberNominees(toMemberNomineeResponseList(memberDetail.getMemberNominees()));
    responseDto.setMemberFamilies(toMemberFamilyResponseList(memberDetail.getMemberFamilies()));
}

private String joinNonNullTrimmed(String... parts) {
    return Arrays.stream(parts)
        .filter(p -> p != null && !p.isBlank())
        .collect(Collectors.joining(" "));
}

private List<MemberBankResponseDto> toMemberBankResponseList(List<MemberBank> banks) {
        if (banks == null || banks.isEmpty())
            return List.of();

        return banks.stream()
                .map(bank -> MemberBankResponseDto.builder()
                        .id(bank.getId())
                        .actNumber(bank.getActNumber())
                        .holderName(bank.getHolderName())
                        .memberCode(bank.getMember().getMemberCode())
                        .accountType(bank.getAccountType())
                        .bankId(bank.getBankId())
                        .bankName(getBankName(bank.getBankId()))
                        .isDefault(bank.getIsDefault())
                        .build())
                .toList();
    }
    private String getBankName(Long bankId) {
        return bankTypeRepository.findById(bankId)
                .orElseThrow(() -> ClaimException.notFound("Bank not found with ID: " + bankId))
                .getBankTypeName();
    }
    private List<MemberNomineeResponseDto> toMemberNomineeResponseList(List<MemberNominee> nominees) {
        if (nominees == null || nominees.isEmpty())
            return List.of();

        return nominees.stream()
                .map(nominee -> MemberNomineeResponseDto.builder()
                        .id(nominee.getId())
                        .fullName(getFullName(nominee.getFirstName(), nominee.getMiddleName(), nominee.getLastName()))
                        .memberCode(nominee.getMember().getMemberCode())
                        .relationName(getRelationTypeName(nominee.getRelationId()))
                        .identityNumber(nominee.getIdentityNumber())
                        .identityTypeName(getPersonIdentityType(nominee.getIdentityTypeId()))
                        .dateOfBirth(nominee.getDateOfBirth())
                        .sharePercentage(nominee.getSharePercentage())
                        .build())
                .toList();
    }

    private List<MemberFamilyResponseDto> toMemberFamilyResponseList(List<MemberFamily> families) {
        if (families == null || families.isEmpty())
            return List.of();

        return families.stream()
                .map(family -> MemberFamilyResponseDto.builder()
                        .id(family.getId())
                        .fullName(getFullName(family.getFirstName(), family.getMiddleName(), family.getLastName()))
                        .identityNumber(family.getIdentityNumber())
                        .identityTypeName(getPersonIdentityType(family.getIdentityTypeId()))
                        .memberCode(family.getMember().getMemberCode())
                        .relationName(getRelationTypeName(family.getRelationId()))
                        .dateOfBirth(family.getDateOfBirth())
                        .build())
                .toList();
    }

    private String getPersonIdentityType(Long identityTypeId) {
        return personIdentityRepository.findById(identityTypeId)
                .orElseThrow(() -> ClaimException.notFound("Person identity type not found with ID: " + identityTypeId))
                .getName();
    }

    private String getRelationTypeName(Long relationId) {
        return relationTypeRepository.findById(relationId)
                .orElseThrow(() -> ClaimException.notFound("Relation type not found with ID: " + relationId))
                .getRelationTypeName();
    }

    private String getFullName(String firstName, String middleName, String lastName) {
        return joinNonNullTrimmed(firstName, middleName, lastName);
    }
}
