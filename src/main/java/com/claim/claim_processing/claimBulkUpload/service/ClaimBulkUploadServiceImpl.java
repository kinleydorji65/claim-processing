// package com.claim.claim_processing.claimBulkUpload.service;


// import com.claim.claim_processing.application.entity.application.ClaimApplication;
// import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
// import com.claim.claim_processing.claimBulkUpload.dto.ClaimBulkUploadRequestDTO;
// import com.claim.claim_processing.claimBulkUpload.dto.ClaimBulkUploadResponseDTO;
// import com.claim.claim_processing.claimBulkUpload.dto.ClaimBulkUploadResultDTO;
// import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
// import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
// import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
// import com.claim.claim_processing.common.entities.contribution.SchemeType;
// import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
// import com.claim.claim_processing.common.entities.others.member.MemberDetail;
// import com.claim.claim_processing.common.repository.claim.ClaimTypeMasterRepository;
// import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
// import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
// import com.claim.claim_processing.exceptions.ClaimException;
// import com.claim.claim_processing.integration.client.MasterCodeGenClient;

// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.sql.Date;
// import java.time.LocalDate;
// import java.util.*;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class ClaimBulkUploadServiceImpl implements ClaimBulkUploadService {


//     private final ClaimApplicationRepository claimRepository;
//     private final MemberDetailRepository memberRepository;
//     private final ClaimTypeMasterRepository claimTypeRepository;
//     private final SchemeTypeRepository schemeTypeRepository;
//     private final SubmissionChannelMaster submissionChannelRepository;
//     private final MasterCodeGenClient masterCodeGenClient;
//     // private final ClaimBulkUploadPersistService persistService;

//     @Value("${app.codegen.application.code-type}")
//     private String applicationCodeType;

//     @Value("${app.codegen.application.claim-prefix}")
//     private String claimPrefix;

//     @Value("${app.templates.base-path}")
//     private String templateBasePath;

//     @Value("${app.templates.claim-bulk-upload}")
//     private String claimBulkUploadTemplate;

//     private static final String CLAIM_TYPE_WITHDRAWAL = "1";
//     private static final String CLAIM_TYPE_PENSION = "2";
//     private static final String CLAIM_TYPE_BENEFICIARY = "3";
//     private static final String CLAIM_TYPE_LEGAL = "4";

//     @Getter
//     public static class PreparedClaimRow {
//         int rowNumber;
//         String generatedApplicationNumber;
//         ClaimApplication claimApplication;
//         // Store original values for response
//         String identityNumber;
//         String memberCode;
//         String nppfNumber;
//         String submissionChannel;
//         Date applicationDate;
//         String agencyCode;
//         String agencyName;
//         String onBehalfOfMember;
//         ActivityEnum isSpecialCase;
//         BigDecimal numberOfYearInService;
//         String createdBy;
//         String claimTypeId;
//     }

//     // @Override
//     // @Transactional(readOnly = true)
//     // public ClaimBulkUploadResponseDTO uploadClaims(List<ClaimBulkUploadRequestDTO> requests) {
//     public ClaimBulkUploadResponseDTO uploadClaims(List<ClaimBulkUploadRequestDTO> requests) {

//         if (requests == null || requests.isEmpty()) {
//             throw ClaimException.badRequest("Request body is required and cannot be empty");
//         }

//         Optional<MemberDetail> existingMember;
//         for (ClaimBulkUploadRequestDTO req : requests) {
//             if (req.getAgencyCode() == null) {
//                 throw ClaimException.badRequest("Request body is required and cannot be empty");
//             }
//             existingMember = memberRepository.findByNppfNumber(req.getNppfNumber());
//         }
//         // Get existing members for this agency
        

//         ClaimBulkUploadResponseDTO response = ClaimBulkUploadResponseDTO.builder()
//                 .rows(new ArrayList<>())
//                 .build();

//         Set<String> processedIdentityInRequest = new HashSet<>();
//         List<PreparedClaimRow> preparedRows = new ArrayList<>();

//         int total = requests.size();
//         int success = 0;
//         int fail = 0;

//         for (int i = 0; i < requests.size(); i++) {

//             ClaimBulkUploadRequestDTO row = requests.get(i);
//             int rowNumber = i + 1;

//             String identityNumber = safe(row.getIdentityNumber());
//             String memberCode = safe(row.getMemberCode());
//             String nppfNumber = safe(row.getNppfNumber());
//             String submissionChannel = safe(row.getSubmissionChannel());
//             String onBehalfOfMember = safe(row.getOnBehalfFoMember());
//             String contactNo = safe(row.getContactNo());
//             String email = safe(row.getEmail());

//             String generatedApplicationNumber = null;
//             String claimTypeId = null;

//             try {
//                 // Validate required fields
//                 if (identityNumber.isEmpty()) {
//                     throw ClaimException.badRequest("Identity Number is required (row " + rowNumber + ")");
//                 }
//                 if (nppfNumber.isEmpty()) {
//                     throw ClaimException.badRequest("NPPF Number is required (row " + rowNumber + ")");
//                 }
//                 if (submissionChannel.isEmpty()) {
//                     throw ClaimException.badRequest("Submission Channel is required (row " + rowNumber + ")");
//                 }
//                 if (row.getApplicationDate() == null) {
//                     throw ClaimException.badRequest("Application Date is required (row " + rowNumber + ")");
//                 }

//                 // Check for duplicates in request
//                 String identityKey = identityNumber + ":" + nppfNumber;
//                 if (!processedIdentityInRequest.add(identityKey)) {
//                     throw ClaimException.conflict("Duplicate member found within request body (row " + rowNumber + ")");
//                 }

//                 // Check if member exists in system
//                 MemberDetail member = findMemberByIdentity(identityNumber, existingMember);
//                 if (member == null) {
//                     throw ClaimException.notFound("Member not found with identity number: " + identityNumber + " (row " + rowNumber + ")");
//                 }

//                 // Check if member code matches
//                 if (!memberCode.isEmpty() && !member.getMemberCode().equals(memberCode)) {
//                     throw ClaimException.badRequest("Member code does not match for identity number: " + identityNumber + " (row " + rowNumber + ")");
//                 }

//                 // Get claim type from submission channel or default
//                 claimTypeId = getClaimTypeFromSubmissionChannel(submissionChannel);
//                 if (claimTypeId == null) {
//                     throw ClaimException.badRequest("Invalid submission channel: " + submissionChannel + " (row " + rowNumber + ")");
//                 }

//                 // Check for duplicate claim application
//                 // if (checkIfClaimExists(identityNumber, claimTypeId)) {
//                 //     throw ClaimException.conflict("Member already has a claim application of this type (row " + rowNumber + ")");
//                 // }

//                 // Fetch master data
//                 ClaimTypeMaster claimType = claimTypeRepository.findById(Long.parseLong(claimTypeId))
//                         .orElseThrow(() -> ClaimException.notFound("Claim type not found: " + claimTypeId + " (row " + rowNumber + ")"));

//                 SubmissionChannelMaster channel = submissionChannelRepository.findByChannelName(submissionChannel)
//                         .orElseThrow(() -> ClaimException.notFound("Submission channel not found: " + submissionChannel + " (row " + rowNumber + ")"));

//                 // Generate application number
//                 generatedApplicationNumber = claimApplicationGenerator.generateApplicationNumber(
//                         applicationCodeType, claimPrefix);

//                 // Build Claim Application
//                 ClaimApplication claimApplication = ClaimApplication.builder()
//                         .applicationNumber(generatedApplicationNumber)
//                         .identityNumber(identityNumber)
//                         .memberCode(member.getMemberCode())
//                         .nppfNumber(nppfNumber)
//                         .contactNo(contactNo)
//                         .email(email)
//                         .agencyCode(agencyCode)
//                         .applicationDate(row.getApplicationDate().toLocalDate())
//                         .onBehalfOfMember(onBehalfOfMember.isEmpty() ? "N" : onBehalfOfMember)
//                         .createdBy("SYSTEM")
//                         .claimType(claimType)
//                         .submissionChannel(channel)
//                         .isSpecialCase(row.getIsSpecialCase() != null ? row.getIsSpecialCase() : ActivityEnum.N)
//                         .numberOfYearInService(row.getNumberOfYearInService())
//                         .isActive(ActivityEnum.Y)
//                         .build();

//                 PreparedClaimRow pr = new PreparedClaimRow();
//                 pr.rowNumber = rowNumber;
//                 pr.generatedApplicationNumber = generatedApplicationNumber;
//                 pr.claimApplication = claimApplication;
//                 pr.identityNumber = identityNumber;
//                 pr.memberCode = member.getMemberCode();
//                 pr.nppfNumber = nppfNumber;
//                 pr.submissionChannel = submissionChannel;
//                 pr.applicationDate = row.getApplicationDate();
//                 pr.agencyCode = agencyCode;
//                 pr.agencyName = agency.getAgencyName();
//                 pr.onBehalfOfMember = onBehalfOfMember;
//                 pr.isSpecialCase = claimApplication.getIsSpecialCase();
//                 pr.numberOfYearInService = row.getNumberOfYearInService();
//                 pr.claimTypeId = claimTypeId;

//                 preparedRows.add(pr);
//                 existingMembers.add(member);

//                 success++;
//                 response.getRows().add(ClaimBulkUploadResultDTO.builder()
//                         .rowNumber(rowNumber)
//                         .applicationNumber(generatedApplicationNumber)
//                         .identityNumber(identityNumber)
//                         .memberCode(member.getMemberCode())
//                         .nppfNumber(nppfNumber)
//                         .submissionChannel(submissionChannel)
//                         .applicationDate(row.getApplicationDate())
//                         .status("SUCCESS")
//                         .agencyCode(agencyCode)
//                         .agencyName(agency.getAgencyName())
//                         .onBehalfFoMember(onBehalfOfMember)
//                         .isSpecialCase(claimApplication.getIsSpecialCase())
//                         .numberOfYearInService(row.getNumberOfYearInService())
//                         .createdAt(String.valueOf(System.currentTimeMillis()))
//                         .message("Validated successfully")
//                         .build());

//             } catch (Exception ex) {
//                 fail++;
//                 response.getRows().add(ClaimBulkUploadResultDTO.builder()
//                         .rowNumber(rowNumber)
//                         .applicationNumber(generatedApplicationNumber)
//                         .identityNumber(identityNumber)
//                         .memberCode(memberCode)
//                         .nppfNumber(nppfNumber)
//                         .submissionChannel(submissionChannel)
//                         .applicationDate(row.getApplicationDate())
//                         .status("FAIL")
//                         .agencyCode(agencyCode)
//                         .agencyName(agency.getAgencyName())
//                         .onBehalfFoMember(onBehalfOfMember)
//                         .createdAt(String.valueOf(System.currentTimeMillis()))
//                         .message(cleanDbError(ex))
//                         .build());
//             }
//         }

//         response.setTotalRows(total);
//         response.setSuccessCount(success);
//         response.setFailCount(fail);

//         if (fail > 0) {
//             return response;
//         }

//         persistService.persistClaimsOrRollback(preparedRows);

//         response.getRows().forEach(r -> {
//             if ("SUCCESS".equalsIgnoreCase(r.getStatus())) {
//                 r.setMessage("Saved successfully");
//             }
//         });

//         return response;
//     }

//     private String safe(String s) {
//         return s == null ? "" : s.trim();
//     }

//     private String getClaimTypeFromSubmissionChannel(String submissionChannel) {
//         if (submissionChannel == null || submissionChannel.isEmpty()) {
//             return null;
//         }
//         String channel = submissionChannel.toLowerCase();
//         if (channel.contains("withdrawal") || channel.contains("withdraw")) {
//             return CLAIM_TYPE_WITHDRAWAL;
//         } else if (channel.contains("pension")) {
//             return CLAIM_TYPE_PENSION;
//         } else if (channel.contains("beneficiary")) {
//             return CLAIM_TYPE_BENEFICIARY;
//         } else if (channel.contains("legal")) {
//             return CLAIM_TYPE_LEGAL;
//         }
//         return CLAIM_TYPE_WITHDRAWAL; // Default
//     }

//     private MemberDetail findMemberByIdentity(String identityNumber, MemberDetail member) {
        
//             if (member.getIdentityNumber() != null && member.getIdentityNumber().equals(identityNumber)) {
//                 return member;
//             }
//         return null;
//     }

//     // private boolean checkIfClaimExists(String identityNumber, String claimTypeId) {
//     //     Optional<ClaimApplication> calim = claimRepository.findByIdentityNumber(identityNumber);
//     //     // for (ClaimApplication claim : existingClaims) {
//     //         if (claim.getClaimType() != null && 
//     //             claim.getClaimType().getId().toString().equals(claimTypeId) &&
//     //             claim.getIsActive() == ActivityEnum.Y) {
//     //             return true;
//     //         }
//     //     // }
//     //     return false;
//     // }

//     private String cleanDbError(Exception ex) {
//         String msg = ex.getMessage();
//         if (msg == null)
//             return "Error while processing row.";
//         msg = msg.replaceAll("\\s+", " ").trim();
//         return msg.length() > 300 ? msg.substring(0, 300) : msg;
//     }

//     // @Override
//     // public Resource getClaimBulkUploadTemplate() {
//     //     String fullPath = templateBasePath + "/" + claimBulkUploadTemplate;
//     //     ClassPathResource resource = new ClassPathResource(fullPath);
//     //     if (!resource.exists()) {
//     //         throw ClaimException.notFound("Template not found: " + fullPath);
//     //     }
//     //     return resource;
//     // }
// }
