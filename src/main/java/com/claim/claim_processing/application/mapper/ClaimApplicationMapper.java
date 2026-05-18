// package com.claim.claim_processing.application.mapper;

// import com.claim.claim_processing.application.dto.request.ClaimApplicationCreateRequestDto;
// import com.claim.claim_processing.application.dto.response.ClaimApplicationResponseDto;
// import com.claim.claim_processing.claim.entity.application.ClaimApplication;
// import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
// import org.mapstruct.*;

// @Mapper(
//         componentModel = "spring",
//         unmappedTargetPolicy = ReportingPolicy.IGNORE
// )
// public interface ClaimApplicationMapper {

//     // =========================================
//     // CREATE DTO -> ENTITY
//     // =========================================

//     @Mapping(target = "id", ignore = true)
//     @Mapping(target = "applicationNumber", ignore = true)
//     @Mapping(target = "status", ignore = true)
//     @Mapping(target = "action", ignore = true)
//     @Mapping(target = "isActive", constant = "Y")
//     @Mapping(target = "normalClaimDetail", ignore = true)
//     @Mapping(target = "partialWithdrawalDetail", ignore = true)
//     @Mapping(target = "beneficiarySettlementDetail", ignore = true)
//     @Mapping(target = "excessRefundDetail", ignore = true)
//     @Mapping(target = "legalRecoveryDetail", ignore = true)
//     @Mapping(target = "wrongRemittanceDetail", ignore = true)

//     ClaimApplication toEntity(
//             ClaimApplicationCreateRequestDto dto
//     );

//     // =========================================
//     // ENTITY -> RESPONSE DTO
//     // =========================================

//     @Mapping(source = "claimType.id", target = "claimTypeId")
//     @Mapping(source = "claimType.name", target = "claimTypeName")

//     @Mapping(source = "claimSource.id", target = "claimSourceId")
//     @Mapping(source = "claimSource.name", target = "claimSourceName")

//     @Mapping(source = "submissionChannel.id", target = "submissionChannelId")
//     @Mapping(source = "submissionChannel.name", target = "submissionChannelName")

//     @Mapping(source = "schemeType.id", target = "schemeTypeId")
//     @Mapping(source = "schemeType.name", target = "schemeTypeName")

//     @Mapping(source = "memberCategory.categoryId", target = "memberCategoryId")
//     @Mapping(source = "memberCategory.categoryName", target = "memberCategoryName")

//     @Mapping(source = "officeId", target = "officeId")

//     @Mapping(source = "specialCaseAuthority.id", target = "specialCaseAuthorityId")
//     @Mapping(source = "specialCaseAuthority.name", target = "specialCaseAuthorityName")

//     @Mapping(source = "currentStage.id", target = "currentStageId")
//     @Mapping(source = "currentStage.name", target = "currentStageName")

//     @Mapping(source = "status.statusId", target = "statusId")

//     @Mapping(source = "action.id", target = "actionId")
//     @Mapping(source = "action.name", target = "actionName")

//     ClaimApplicationResponseDto toResponseDto(
//             ClaimApplication entity
//     );
// }
