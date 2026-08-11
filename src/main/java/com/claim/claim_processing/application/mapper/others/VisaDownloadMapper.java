package com.claim.claim_processing.application.mapper.others;

import com.claim.claim_processing.application.DTO.request.others.VisaDownloadRequest;
import com.claim.claim_processing.application.DTO.response.others.VisaDownloadResponse;
import com.claim.claim_processing.application.entity.others.VisaDownloaded;
import com.claim.claim_processing.application.entity.others.VisaFinancialYearData;
import com.claim.claim_processing.application.entity.others.VisaResponseData;
import org.mapstruct.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class, Timestamp.class, Instant.class})
public interface VisaDownloadMapper {

    // ============ VISA DOWNLOADED MAPPINGS ============
    
    /**
     * Convert VisaDownloadRequest to VisaDownloaded Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "downloadedDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "submissionChannelId", source = "submissionChannelId")
    @Mapping(target = "ipAddress", source = "ipAddress")
    @Mapping(target = "deviceName", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "nppfNumber", source = "nppfNumber")
    @Mapping(target = "cid", source = "cid")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "visaResponseData", ignore = true)
    VisaDownloaded toVisaDownloadedEntity(VisaDownloadRequest request);

    /**
     * Update existing VisaDownloaded Entity from Request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "downloadedDate", ignore = true)
    @Mapping(target = "submissionChannelId", source = "submissionChannelId")
    @Mapping(target = "ipAddress", source = "ipAddress")
    @Mapping(target = "deviceName", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "nppfNumber", source = "nppfNumber")
    @Mapping(target = "cid", source = "cid")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "visaResponseData", ignore = true)
    void updateVisaDownloadedEntity(@MappingTarget VisaDownloaded entity, 
                                     VisaDownloadRequest request);

    // ============ VISA RESPONSE DATA MAPPINGS ============
    
    /**
     * Convert VisaResponseDataRequest to VisaResponseData Entity
     * FK fields are ignored - will be set in service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visaDownloaded", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "financialYearData", ignore = true)
    @Mapping(target = "joiningDate", source = "joiningDate")
    @Mapping(target = "calculationDate", source = "calculationDate")
    @Mapping(target = "firstContributionDate", source = "firstContributionDate")
    @Mapping(target = "lastContributionDate", source = "lastContributionDate")
    @Mapping(target = "totalPrincipal", source = "totalPrincipal")
    @Mapping(target = "totalInterest", source = "totalInterest")
    @Mapping(target = "totalBalance", source = "totalBalance")
    @Mapping(target = "currentYear", source = "currentYear")
    @Mapping(target = "currentYearRate", source = "currentYearRate")
    @Mapping(target = "currentYearBasis", source = "currentYearBasis")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "responseMessage", source = "responseMessage")
    @Mapping(target = "createdBy", source = "createdBy")
    VisaResponseData toVisaResponseDataEntity(VisaDownloadRequest.VisaResponseDataRequest request);

    /**
     * Convert VisaResponseData Entity to VisaResponseDataResponse
     */
    @Mapping(target = "visaDownloadedId", source = "visaDownloaded.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "joiningDate", source = "joiningDate")
    @Mapping(target = "calculationDate", source = "calculationDate")
    @Mapping(target = "firstContributionDate", source = "firstContributionDate")
    @Mapping(target = "lastContributionDate", source = "lastContributionDate")
    @Mapping(target = "totalPrincipal", source = "totalPrincipal")
    @Mapping(target = "totalInterest", source = "totalInterest")
    @Mapping(target = "totalBalance", source = "totalBalance")
    @Mapping(target = "currentYear", source = "currentYear")
    @Mapping(target = "currentYearRate", source = "currentYearRate")
    @Mapping(target = "currentYearBasis", source = "currentYearBasis")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "responseMessage", source = "responseMessage")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "visaFinancialYearDataResponse", source = "financialYearData")
    VisaDownloadResponse.VisaResponseDataResponse toVisaResponseDataResponse(VisaResponseData entity);

    /**
     * Update existing VisaResponseData Entity from Request
     * FK fields are ignored - will be set in service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visaDownloaded", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "financialYearData", ignore = true)
    @Mapping(target = "joiningDate", source = "joiningDate")
    @Mapping(target = "calculationDate", source = "calculationDate")
    @Mapping(target = "firstContributionDate", source = "firstContributionDate")
    @Mapping(target = "lastContributionDate", source = "lastContributionDate")
    @Mapping(target = "totalPrincipal", source = "totalPrincipal")
    @Mapping(target = "totalInterest", source = "totalInterest")
    @Mapping(target = "totalBalance", source = "totalBalance")
    @Mapping(target = "currentYear", source = "currentYear")
    @Mapping(target = "currentYearRate", source = "currentYearRate")
    @Mapping(target = "currentYearBasis", source = "currentYearBasis")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "responseMessage", source = "responseMessage")
    @Mapping(target = "createdBy", source = "createdBy")
    void updateVisaResponseDataEntity(@MappingTarget VisaResponseData entity, 
                                       VisaDownloadRequest.VisaResponseDataRequest request);

    // ============ VISA FINANCIAL YEAR DATA MAPPINGS ============
    
    /**
     * Convert VisaFinancialYearDataRequest to VisaFinancialYearData Entity
     * FK fields are ignored - will be set in service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visaResponseData", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "financialYear", source = "financialYear")
    @Mapping(target = "openingPfMc", source = "openingPfMc")
    @Mapping(target = "openingPfEc", source = "openingPfEc")
    @Mapping(target = "openingPfImc", source = "openingPfImc")
    @Mapping(target = "openingPfIec", source = "openingPfIec")
    @Mapping(target = "openingPfTotal", source = "openingPfTotal")
    @Mapping(target = "openingPcMc", source = "openingPcMc")
    @Mapping(target = "openingPcEc", source = "openingPcEc")
    @Mapping(target = "openingPcImc", source = "openingPcImc")
    @Mapping(target = "openingPcIec", source = "openingPcIec")
    @Mapping(target = "openingPcTotal", source = "openingPcTotal")
    @Mapping(target = "openingGrandTotal", source = "openingGrandTotal")
    @Mapping(target = "transactionPfMc", source = "transactionPfMc")
    @Mapping(target = "transactionPfEc", source = "transactionPfEc")
    @Mapping(target = "transactionPfImc", source = "transactionPfImc")
    @Mapping(target = "transactionPfIec", source = "transactionPfIec")
    @Mapping(target = "transactionPfTotal", source = "transactionPfTotal")
    @Mapping(target = "transactionPcMc", source = "transactionPcMc")
    @Mapping(target = "transactionPcEc", source = "transactionPcEc")
    @Mapping(target = "transactionPcImc", source = "transactionPcImc")
    @Mapping(target = "transactionPcIec", source = "transactionPcIec")
    @Mapping(target = "transactionPcTotal", source = "transactionPcTotal")
    @Mapping(target = "transactionGrandTotal", source = "transactionGrandTotal")
    @Mapping(target = "excessPcMc", source = "excessPcMc")
    @Mapping(target = "excessPcEc", source = "excessPcEc")
    @Mapping(target = "excessPcImc", source = "excessPcImc")
    @Mapping(target = "excessPcIec", source = "excessPcIec")
    @Mapping(target = "excessTotal", source = "excessTotal")
    @Mapping(target = "closingPfMc", source = "closingPfMc")
    @Mapping(target = "closingPfEc", source = "closingPfEc")
    @Mapping(target = "closingPfImc", source = "closingPfImc")
    @Mapping(target = "closingPfIec", source = "closingPfIec")
    @Mapping(target = "closingPfTotal", source = "closingPfTotal")
    @Mapping(target = "closingPcMc", source = "closingPcMc")
    @Mapping(target = "closingPcEc", source = "closingPcEc")
    @Mapping(target = "closingPcImc", source = "closingPcImc")
    @Mapping(target = "closingPcIec", source = "closingPcIec")
    @Mapping(target = "closingPcTotal", source = "closingPcTotal")
    @Mapping(target = "closingGrandTotal", source = "closingGrandTotal")
    VisaFinancialYearData toVisaFinancialYearDataEntity(VisaDownloadRequest.VisaFinancialYearDataRequest request);

    /**
     * Convert VisaFinancialYearData Entity to VisaFinancialYearDataResponse
     */
    @Mapping(target = "visaResponseDataId", source = "visaResponseData.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "financialYear", source = "financialYear")
    @Mapping(target = "openingPfMc", source = "openingPfMc")
    @Mapping(target = "openingPfEc", source = "openingPfEc")
    @Mapping(target = "openingPfImc", source = "openingPfImc")
    @Mapping(target = "openingPfIec", source = "openingPfIec")
    @Mapping(target = "openingPfTotal", source = "openingPfTotal")
    @Mapping(target = "openingPcMc", source = "openingPcMc")
    @Mapping(target = "openingPcEc", source = "openingPcEc")
    @Mapping(target = "openingPcImc", source = "openingPcImc")
    @Mapping(target = "openingPcIec", source = "openingPcIec")
    @Mapping(target = "openingPcTotal", source = "openingPcTotal")
    @Mapping(target = "openingGrandTotal", source = "openingGrandTotal")
    @Mapping(target = "transactionPfMc", source = "transactionPfMc")
    @Mapping(target = "transactionPfEc", source = "transactionPfEc")
    @Mapping(target = "transactionPfImc", source = "transactionPfImc")
    @Mapping(target = "transactionPfIec", source = "transactionPfIec")
    @Mapping(target = "transactionPfTotal", source = "transactionPfTotal")
    @Mapping(target = "transactionPcMc", source = "transactionPcMc")
    @Mapping(target = "transactionPcEc", source = "transactionPcEc")
    @Mapping(target = "transactionPcImc", source = "transactionPcImc")
    @Mapping(target = "transactionPcIec", source = "transactionPcIec")
    @Mapping(target = "transactionPcTotal", source = "transactionPcTotal")
    @Mapping(target = "transactionGrandTotal", source = "transactionGrandTotal")
    @Mapping(target = "excessPcMc", source = "excessPcMc")
    @Mapping(target = "excessPcEc", source = "excessPcEc")
    @Mapping(target = "excessPcImc", source = "excessPcImc")
    @Mapping(target = "excessPcIec", source = "excessPcIec")
    @Mapping(target = "excessTotal", source = "excessTotal")
    @Mapping(target = "closingPfMc", source = "closingPfMc")
    @Mapping(target = "closingPfEc", source = "closingPfEc")
    @Mapping(target = "closingPfImc", source = "closingPfImc")
    @Mapping(target = "closingPfIec", source = "closingPfIec")
    @Mapping(target = "closingPfTotal", source = "closingPfTotal")
    @Mapping(target = "closingPcMc", source = "closingPcMc")
    @Mapping(target = "closingPcEc", source = "closingPcEc")
    @Mapping(target = "closingPcImc", source = "closingPcImc")
    @Mapping(target = "closingPcIec", source = "closingPcIec")
    @Mapping(target = "closingPcTotal", source = "closingPcTotal")
    @Mapping(target = "closingGrandTotal", source = "closingGrandTotal")
    @Mapping(target = "createdAt", source = "createdAt")
    VisaDownloadResponse.VisaFinancialYearDataResponse toVisaFinancialYearDataResponse(VisaFinancialYearData entity);

    /**
     * Update existing VisaFinancialYearData Entity from Request
     * FK fields are ignored - will be set in service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visaResponseData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "financialYear", source = "financialYear")
    @Mapping(target = "openingPfMc", source = "openingPfMc")
    @Mapping(target = "openingPfEc", source = "openingPfEc")
    @Mapping(target = "openingPfImc", source = "openingPfImc")
    @Mapping(target = "openingPfIec", source = "openingPfIec")
    @Mapping(target = "openingPfTotal", source = "openingPfTotal")
    @Mapping(target = "openingPcMc", source = "openingPcMc")
    @Mapping(target = "openingPcEc", source = "openingPcEc")
    @Mapping(target = "openingPcImc", source = "openingPcImc")
    @Mapping(target = "openingPcIec", source = "openingPcIec")
    @Mapping(target = "openingPcTotal", source = "openingPcTotal")
    @Mapping(target = "openingGrandTotal", source = "openingGrandTotal")
    @Mapping(target = "transactionPfMc", source = "transactionPfMc")
    @Mapping(target = "transactionPfEc", source = "transactionPfEc")
    @Mapping(target = "transactionPfImc", source = "transactionPfImc")
    @Mapping(target = "transactionPfIec", source = "transactionPfIec")
    @Mapping(target = "transactionPfTotal", source = "transactionPfTotal")
    @Mapping(target = "transactionPcMc", source = "transactionPcMc")
    @Mapping(target = "transactionPcEc", source = "transactionPcEc")
    @Mapping(target = "transactionPcImc", source = "transactionPcImc")
    @Mapping(target = "transactionPcIec", source = "transactionPcIec")
    @Mapping(target = "transactionPcTotal", source = "transactionPcTotal")
    @Mapping(target = "transactionGrandTotal", source = "transactionGrandTotal")
    @Mapping(target = "excessPcMc", source = "excessPcMc")
    @Mapping(target = "excessPcEc", source = "excessPcEc")
    @Mapping(target = "excessPcImc", source = "excessPcImc")
    @Mapping(target = "excessPcIec", source = "excessPcIec")
    @Mapping(target = "excessTotal", source = "excessTotal")
    @Mapping(target = "closingPfMc", source = "closingPfMc")
    @Mapping(target = "closingPfEc", source = "closingPfEc")
    @Mapping(target = "closingPfImc", source = "closingPfImc")
    @Mapping(target = "closingPfIec", source = "closingPfIec")
    @Mapping(target = "closingPfTotal", source = "closingPfTotal")
    @Mapping(target = "closingPcMc", source = "closingPcMc")
    @Mapping(target = "closingPcEc", source = "closingPcEc")
    @Mapping(target = "closingPcImc", source = "closingPcImc")
    @Mapping(target = "closingPcIec", source = "closingPcIec")
    @Mapping(target = "closingPcTotal", source = "closingPcTotal")
    @Mapping(target = "closingGrandTotal", source = "closingGrandTotal")
    void updateVisaFinancialYearDataEntity(@MappingTarget VisaFinancialYearData entity, 
                                            VisaDownloadRequest.VisaFinancialYearDataRequest request);

    // ============ COMPLEX MAPPINGS ============
    
    /**
     * Convert Complete VisaDownloadRequest to VisaDownloaded with nested entities
     * FK relationships are NOT set here - will be set in service
     */
    default VisaDownloaded toCompleteEntity(VisaDownloadRequest request) {
        if (request == null) {
            return null;
        }

        // Map main entity
        VisaDownloaded entity = toVisaDownloadedEntity(request);
        
        // Map nested VisaResponseData if present (FKs will be set in service)
        if (request.getVisaResponseDataRequest() != null) {
            VisaResponseData responseData = toVisaResponseDataEntity(request.getVisaResponseDataRequest());
            
            // Map nested VisaFinancialYearData if present (FKs will be set in service)
            if (request.getVisaResponseDataRequest().getFinancialYearDataRequest() != null) {
                VisaFinancialYearData financialYearData = toVisaFinancialYearDataEntity(
                        request.getVisaResponseDataRequest().getFinancialYearDataRequest());
                responseData.setFinancialYearData(financialYearData);
            }
            
            // Set the relationship (will be completed in service with FK)
            entity.setVisaResponseData(responseData);
        }
        
        return entity;
    }

    /**
     * Convert Complete VisaDownloaded to VisaDownloadResponse with nested DTOs
     * This is the MAIN mapping method for converting Entity to Response
     */
    default VisaDownloadResponse toResponse(VisaDownloaded entity) {
        if (entity == null) {
            return null;
        }

        // Map main response
        VisaDownloadResponse response = VisaDownloadResponse.builder()
                .id(entity.getId())
                .location(entity.getLocation())
                .submissionChannelId(entity.getSubmissionChannelId())
                .submissionChannelName(entity.getSubmissionChannel() != null ? 
                        entity.getSubmissionChannel().getName() : null)
                .downloadedDate(entity.getDownloadedDate())
                .deviceName(entity.getDeviceName())
                .ipAddress(entity.getIpAddress())
                .nppfNumber(entity.getNppfNumber())
                .cid(entity.getCid())
                .createdAt(entity.getCreatedAt() != null ? 
                        entity.getCreatedAt().toLocalDateTime() : null)
                .createdBy(entity.getCreatedBy())
                .build();
        
        // Map nested VisaResponseData if present
        if (entity.getVisaResponseData() != null) {
            VisaResponseData responseData = entity.getVisaResponseData();
            VisaDownloadResponse.VisaResponseDataResponse responseDataResponse = 
                    toVisaResponseDataResponse(responseData);
            
            // Map nested VisaFinancialYearData if present
            if (responseData.getFinancialYearData() != null) {
                VisaDownloadResponse.VisaFinancialYearDataResponse financialYearResponse = 
                        toVisaFinancialYearDataResponse(responseData.getFinancialYearData());
                responseDataResponse.setVisaFinancialYearDataResponse(financialYearResponse);
            }
            
            response.setVisaResponseDataResponse(responseDataResponse);
        }
        
        return response;
    }

    /**
     * Convert List of VisaDownloaded to List of VisaDownloadResponse with nested DTOs
     */
    default List<VisaDownloadResponse> toResponseList(List<VisaDownloaded> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // ============ AFTER MAPPING HANDLERS ============
    
    /**
     * Set default device name if null
     */
    @AfterMapping
    default void setDefaultDeviceName(@MappingTarget VisaDownloaded entity) {
        if (entity != null && entity.getDeviceName() == null) {
            entity.setDeviceName("Unknown Device");
        }
    }

    /**
     * Set default values for VisaResponseData after mapping
     */
    @AfterMapping
    default void setDefaultResponseDataValues(@MappingTarget VisaResponseData entity) {
        if (entity != null) {
            java.math.BigDecimal zero = java.math.BigDecimal.ZERO;
            if (entity.getTotalPrincipal() == null) entity.setTotalPrincipal(zero);
            if (entity.getTotalInterest() == null) entity.setTotalInterest(zero);
            if (entity.getTotalBalance() == null) entity.setTotalBalance(zero);
            if (entity.getCurrentYearRate() == null) entity.setCurrentYearRate(zero);
        }
    }

    /**
     * Set default values for VisaFinancialYearData after mapping
     */
    @AfterMapping
    default void setDefaultFinancialYearDataValues(@MappingTarget VisaFinancialYearData entity) {
        if (entity != null) {
            java.math.BigDecimal zero = java.math.BigDecimal.ZERO;
            
            // Opening Balances - PF (Provident Fund)
            if (entity.getOpeningPfMc() == null) entity.setOpeningPfMc(zero);
            if (entity.getOpeningPfEc() == null) entity.setOpeningPfEc(zero);
            if (entity.getOpeningPfImc() == null) entity.setOpeningPfImc(zero);
            if (entity.getOpeningPfIec() == null) entity.setOpeningPfIec(zero);
            if (entity.getOpeningPfTotal() == null) entity.setOpeningPfTotal(zero);
            
            // Opening Balances - PC (Pension Contribution)
            if (entity.getOpeningPcMc() == null) entity.setOpeningPcMc(zero);
            if (entity.getOpeningPcEc() == null) entity.setOpeningPcEc(zero);
            if (entity.getOpeningPcImc() == null) entity.setOpeningPcImc(zero);
            if (entity.getOpeningPcIec() == null) entity.setOpeningPcIec(zero);
            if (entity.getOpeningPcTotal() == null) entity.setOpeningPcTotal(zero);
            if (entity.getOpeningGrandTotal() == null) entity.setOpeningGrandTotal(zero);
            
            // Transaction - PF
            if (entity.getTransactionPfMc() == null) entity.setTransactionPfMc(zero);
            if (entity.getTransactionPfEc() == null) entity.setTransactionPfEc(zero);
            if (entity.getTransactionPfImc() == null) entity.setTransactionPfImc(zero);
            if (entity.getTransactionPfIec() == null) entity.setTransactionPfIec(zero);
            if (entity.getTransactionPfTotal() == null) entity.setTransactionPfTotal(zero);
            
            // Transaction - PC
            if (entity.getTransactionPcMc() == null) entity.setTransactionPcMc(zero);
            if (entity.getTransactionPcEc() == null) entity.setTransactionPcEc(zero);
            if (entity.getTransactionPcImc() == null) entity.setTransactionPcImc(zero);
            if (entity.getTransactionPcIec() == null) entity.setTransactionPcIec(zero);
            if (entity.getTransactionPcTotal() == null) entity.setTransactionPcTotal(zero);
            if (entity.getTransactionGrandTotal() == null) entity.setTransactionGrandTotal(zero);
            
            // Excess
            if (entity.getExcessPcMc() == null) entity.setExcessPcMc(zero);
            if (entity.getExcessPcEc() == null) entity.setExcessPcEc(zero);
            if (entity.getExcessPcImc() == null) entity.setExcessPcImc(zero);
            if (entity.getExcessPcIec() == null) entity.setExcessPcIec(zero);
            if (entity.getExcessTotal() == null) entity.setExcessTotal(zero);
            
            // Closing - PF
            if (entity.getClosingPfMc() == null) entity.setClosingPfMc(zero);
            if (entity.getClosingPfEc() == null) entity.setClosingPfEc(zero);
            if (entity.getClosingPfImc() == null) entity.setClosingPfImc(zero);
            if (entity.getClosingPfIec() == null) entity.setClosingPfIec(zero);
            if (entity.getClosingPfTotal() == null) entity.setClosingPfTotal(zero);
            
            // Closing - PC
            if (entity.getClosingPcMc() == null) entity.setClosingPcMc(zero);
            if (entity.getClosingPcEc() == null) entity.setClosingPcEc(zero);
            if (entity.getClosingPcImc() == null) entity.setClosingPcImc(zero);
            if (entity.getClosingPcIec() == null) entity.setClosingPcIec(zero);
            if (entity.getClosingPcTotal() == null) entity.setClosingPcTotal(zero);
            if (entity.getClosingGrandTotal() == null) entity.setClosingGrandTotal(zero);
        }
    }

    /**
     * Set bidirectional relationship between VisaDownloaded and VisaResponseData
     */
    @AfterMapping
    default void setBidirectionalRelationships(@MappingTarget VisaDownloaded entity) {
        if (entity != null && entity.getVisaResponseData() != null) {
            VisaResponseData responseData = entity.getVisaResponseData();
            responseData.setVisaDownloaded(entity);
            
            if (responseData.getFinancialYearData() != null) {
                VisaFinancialYearData financialData = responseData.getFinancialYearData();
                financialData.setVisaResponseData(responseData);
            }
        }
    }
}