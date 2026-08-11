package com.claim.claim_processing.integration.member.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.claim.claim_processing.common.DTO.response.others.member.MemberAddressResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberBankResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberFamilyResponseDto;
import com.claim.claim_processing.common.DTO.response.others.member.MemberNomineeResponseDto;
import com.claim.claim_processing.common.entities.others.member.MemberAddress;
import com.claim.claim_processing.common.entities.others.member.MemberBank;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.entities.others.member.MemberFamily;
import com.claim.claim_processing.common.entities.others.member.MemberNominee;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.others.BankTypeRepository;
import com.claim.claim_processing.common.repository.others.EmploymentTypeRepository;
import com.claim.claim_processing.common.repository.others.PersonIdentityRepository;
import com.claim.claim_processing.common.repository.others.RelationTypeRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.client.MasterDataClient;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class MemberDetailMapper {
    @Autowired
    protected PersonIdentityRepository personIdentityRepository;
    @Autowired
    protected EmploymentTypeRepository employmentTypeRepository;
    @Autowired
    protected BankTypeRepository bankTypeRepository;
    @Autowired
    protected RelationTypeRepository relationTypeRepository;
    @Autowired
    protected AgencyCategoryRepository agencyCategoryRepository;
    @Autowired
    protected MasterDataClient masterDataClient;

    
    @Mapping(target = "memberName", ignore = true) // We'll set this in @AfterMapping
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "memberStatus", source = "status")

    public abstract MemberDetailResponseDto toMemberDetailResponseDto(MemberDetail memberDetail);

    @AfterMapping
    protected void setOtherDetails(MemberDetail memberDetail, @MappingTarget MemberDetailResponseDto responseDto) {
        responseDto.setMemberName(
                getFullName(memberDetail.getFirstName(), memberDetail.getMiddleName(), memberDetail.getLastName()));
        String identityTypeName = personIdentityRepository.findById(memberDetail.getIdentityTypeId())
                .orElseThrow(() -> ClaimException.notFound("Identy type not found with ID: " + memberDetail.getId()))
                .getName();
        String employmentTypeName = null;
        if (memberDetail.getWorkInfo() != null && memberDetail.getWorkInfo().getEmploymentTypeId() > 0) {
            employmentTypeName = employmentTypeRepository.findById(memberDetail.getWorkInfo().getEmploymentTypeId())
                    .orElseThrow(() -> ClaimException.notFound(
                            "Employment type not found with ID: " + memberDetail.getWorkInfo().getEmploymentTypeId()))
                    .getEmploymentTypeName();
        }

        responseDto.setDateOfServiceJoiningDate(memberDetail.getWorkInfo().getServiceJoiningDate());
        responseDto.setContactNo(memberDetail.getContactNo() != null && memberDetail.getContactNo() > 0
                ? memberDetail.getContactNo().toString()
                : null);
        responseDto.setEmail(memberDetail.getEmail());
        responseDto.setMemberCategory(getAgencyCategoryName(memberDetail.getAgencyCategoryId()));
        responseDto.setMemberCategoryId(memberDetail.getAgencyCategoryId());
        responseDto.setIdentityTypeName(identityTypeName);
        responseDto.setEmploymentTypeName(employmentTypeName != null ? employmentTypeName : "Unknown");
        responseDto.setMemberStatus(memberDetail.getStatus());
        responseDto.setBasicSalary(
                memberDetail.getWorkInfo().getBasicPay() != null ? memberDetail.getWorkInfo().getBasicPay().toString()
                        : null);
        responseDto.setMemberBanks(toMemberBankResponseList(memberDetail.getMemberBanks()));
        responseDto.setMemberNominees(toMemberNomineeResponseList(memberDetail.getMemberNominees()));
        responseDto.setMemberFamilies(toMemberFamilyResponseList(memberDetail.getMemberFamilies()));
        responseDto.setMemberAddress(toMemberAddress(memberDetail.getAddress()));
        responseDto.setSchemeTypeId(responseDto.getEmploymentTypeName().equals("Regular") ? 1L : 3L);
        responseDto.setAgencyCode(
                memberDetail.getAgencyDetail() != null ? memberDetail.getAgencyDetail().getAgencyCode() : null);
    }

    private String getAgencyCategoryName(String agencyCategoryId) {
        return agencyCategoryRepository.findById(agencyCategoryId)
                .orElseThrow(() -> ClaimException.notFound("Agency category not found with ID: " + agencyCategoryId))
                .getCategoryName();
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

    private MemberAddressResponseDto toMemberAddress(MemberAddress address) {
        if (address == null)
            return null;

        return MemberAddressResponseDto.builder()
                        .id(address.getId())
                .currentCountryId(address.getCurrentCountryId())
                .permanentCountryId(address.getPermanentCountryId())
                .permanentCountryName(getCountryName(address.getPermanentCountryId()))
                .currentCountryName(getCountryName(address.getCurrentCountryId()))
                .nationalityId(address.getNationalityId())
                .nationalityName(getNationalityName(address.getNationalityId()))
                .permanentDzongkhagId(address.getPermanentDzongkhagId())
                .permanentDzongkhagName(getDzongkhagName(address.getPermanentDzongkhagId()))
                .permanentGewogId(address.getPermanentGewogId())
                .permanentGewogName(getGewogName(address.getPermanentGewogId()))
                .permanentVillageId(address.getPermanentVillageId())
                .permanentVillageName(getVillageName(address.getPermanentVillageId()))
                .currentDzongkhagId(address.getCurrentDzongkhagId())
                .currentDzongkhagName(getDzongkhagName(address.getCurrentDzongkhagId()))
                .currentGewogId(address.getCurrentGewogId())
                .currentGewogName(getGewogName(address.getCurrentGewogId()))
                .currentVillageId(address.getCurrentVillageId())
                .currentVillageName(getVillageName(address.getCurrentVillageId()))
                .thramNumber(address.getThramNumber())
                .houseNumber(address.getHouseNumber())
                .householdNumber(address.getHouseholdNumber())
                .streetName(address.getStreetName())
                .currentDistrict(address.getCurrentDistrict())
                .permanentDistrict(address.getPermanentDistrict())
                .currentState(address.getCurrentState())
                .permanentState(address.getPermanentState())
                .currentCity(address.getCurrentCity())
                .permanentCity(address.getPermanentCity())
                .buildingNo(address.getBuildingNo())
                .floorNo(address.getFloorNo())
                        .build();
    }

    private String getDzongkhagName(Long dzongkhagId) {
        if (dzongkhagId == null || dzongkhagId <= 0) return null;
        try {
            return masterDataClient.getDzongkhagById(dzongkhagId).getDzongkhagName();
        } catch (Exception e) {
            return null;
        }
    }

    private String getGewogName(Long gewogId) {
        if (gewogId == null || gewogId <= 0) return null;
        try {
            return masterDataClient.getGewogById(gewogId).getGewogName();
        } catch (Exception e) {
            return null;
        }
    }

    private String getVillageName(Long villageId) {
        if (villageId == null || villageId <= 0) return null;
        try {
            return masterDataClient.getVillageById(villageId).getVillageName();
        } catch (Exception e) {
            return null;
        }
    }

    private String getCountryName(Long countryId) {
        if (countryId == null || countryId <= 0)
            return null;
        try {
            return masterDataClient.getCountryById(countryId).getCountryName();
        } catch (Exception e) {
            return null;
        }
    }

    private String getNationalityName(Long nationalityId) {
        if (nationalityId == null || nationalityId <= 0)
            return null;
        try {
            return masterDataClient.getNationalityById(nationalityId).getNationalityName();
        } catch (Exception e) {
            return null;
        }
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
