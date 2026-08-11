package com.claim.claim_processing.application.service.others.impl;

import com.claim.claim_processing.application.DTO.request.others.VisaDownloadRequest;
import com.claim.claim_processing.application.DTO.response.others.VisaDownloadResponse;
import com.claim.claim_processing.application.entity.others.VisaDownloaded;
import com.claim.claim_processing.application.entity.others.VisaFinancialYearData;
import com.claim.claim_processing.application.entity.others.VisaResponseData;
import com.claim.claim_processing.application.mapper.others.VisaDownloadMapper;
import com.claim.claim_processing.application.repository.others.VisaDownloadedRepository;
import com.claim.claim_processing.application.service.others.VisaDownloadService;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.repository.common.SubmissionChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VisaDownloadServiceImpl implements VisaDownloadService {

    private final VisaDownloadedRepository repository;
    private final SubmissionChannelRepository channelRepository;
    private final VisaDownloadMapper mapper;

    // IP Address validation pattern (IPv4)
    private static final String IPV4_PATTERN = 
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IPV4_PATTERN_COMPILED = Pattern.compile(IPV4_PATTERN);

    @Override
    public VisaDownloadResponse logDownload(VisaDownloadRequest request, String userAgent) {
        log.info("Logging visa download for channel ID: {}", request.getSubmissionChannelId());

        // Validate channel exists
        SubmissionChannelMaster channel = channelRepository.findById(request.getSubmissionChannelId())
                .orElseThrow(() -> new RuntimeException(
                        "Submission channel not found with ID: " + request.getSubmissionChannelId()));

        // Validate channel is active
        if (channel.getIsActive() == null || !"Y".equals(channel.getIsActive().name())) {
            throw new RuntimeException("Submission channel is not active: " + channel.getCode());
        }

        // Validate IP address
        if (request.getIpAddress() != null && !request.getIpAddress().isEmpty()) {
            validateIpAddress(request.getIpAddress());
        }

        // Convert request to complete entity with nested objects
        VisaDownloaded entity = mapper.toCompleteEntity(request);
        
        // Set device name from User-Agent (backend sets it)
        entity.setDeviceName(getDeviceNameFromUserAgent(userAgent));
        
        // Manually set bidirectional relationships for saving
        if (entity.getVisaResponseData() != null) {
            VisaResponseData responseData = entity.getVisaResponseData();
            responseData.setVisaDownloaded(entity);
            
            if (responseData.getFinancialYearData() != null) {
                VisaFinancialYearData financialData = responseData.getFinancialYearData();
                financialData.setVisaResponseData(responseData);
            }
        }
        
        // Save entity (cascade will save all nested entities)
        VisaDownloaded savedEntity = repository.save(entity);
        log.info("Visa download logged successfully with ID: {}", savedEntity.getId());

        // Return complete response with all nested DTOs
        return mapper.toResponse(savedEntity);
    }

    @Override
    public VisaDownloadResponse updateDownload(Long id, VisaDownloadRequest request, String userAgent) {
        log.info("Updating visa download with ID: {}", id);
        
        // Find existing entity
        VisaDownloaded existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visa download not found with ID: " + id));

        // Validate channel if changed
        if (request.getSubmissionChannelId() != null && 
            !request.getSubmissionChannelId().equals(existingEntity.getSubmissionChannelId())) {
            
            SubmissionChannelMaster channel = channelRepository.findById(request.getSubmissionChannelId())
                    .orElseThrow(() -> new RuntimeException(
                            "Submission channel not found with ID: " + request.getSubmissionChannelId()));
            
            if (channel.getIsActive() == null || !"Y".equals(channel.getIsActive().name())) {
                throw new RuntimeException("Submission channel is not active: " + channel.getCode());
            }
        }

        // Validate IP address if provided in request
        if (request.getIpAddress() != null && !request.getIpAddress().isEmpty()) {
            validateIpAddress(request.getIpAddress());
        }

        // Update main entity
        mapper.updateVisaDownloadedEntity(existingEntity, request);

        // Update or create nested VisaResponseData
        if (request.getVisaResponseDataRequest() != null) {
            VisaResponseData responseData = existingEntity.getVisaResponseData();
            
            if (responseData == null) {
                // Create new VisaResponseData
                responseData = mapper.toVisaResponseDataEntity(request.getVisaResponseDataRequest());
                responseData.setVisaDownloaded(existingEntity);
                existingEntity.setVisaResponseData(responseData);
            } else {
                // Update existing VisaResponseData
                mapper.updateVisaResponseDataEntity(responseData, request.getVisaResponseDataRequest());
                // Ensure FK is maintained
                responseData.setVisaDownloaded(existingEntity);
            }
            
            // Update or create VisaFinancialYearData
            if (request.getVisaResponseDataRequest().getFinancialYearDataRequest() != null) {
                VisaFinancialYearData financialData = responseData.getFinancialYearData();
                
                if (financialData == null) {
                    // Create new VisaFinancialYearData
                    financialData = mapper.toVisaFinancialYearDataEntity(
                            request.getVisaResponseDataRequest().getFinancialYearDataRequest());
                    financialData.setVisaResponseData(responseData);
                    responseData.setFinancialYearData(financialData);
                } else {
                    // Update existing VisaFinancialYearData
                    mapper.updateVisaFinancialYearDataEntity(financialData, 
                            request.getVisaResponseDataRequest().getFinancialYearDataRequest());
                    // Ensure FK is maintained
                    financialData.setVisaResponseData(responseData);
                }
            }
        }

        // Update device name from User-Agent if provided (backend sets it)
        if (userAgent != null && !userAgent.isEmpty()) {
            existingEntity.setDeviceName(getDeviceNameFromUserAgent(userAgent));
        }

        // Save updated entity
        VisaDownloaded updatedEntity = repository.save(existingEntity);
        log.info("Visa download updated successfully with ID: {}", updatedEntity.getId());

        // Return complete response with all nested DTOs
        return mapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public VisaDownloadResponse getById(Long id) {
        log.info("Fetching visa download by ID: {}", id);
        
        VisaDownloaded entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visa download not found with ID: " + id));
        
        // Return complete response with nested DTOs
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisaDownloadResponse> getAllDownloads() {
        log.info("Fetching all visa downloads");
        
        List<VisaDownloaded> entities = repository.findAll();
        log.info("Found {} visa downloads", entities.size());
        
        // Return complete responses with nested DTOs
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisaDownloadResponse> getByNppfNumber(String nppfNumber) {
        log.info("Fetching visa downloads by NPPF number: {}", nppfNumber);
        
        if (nppfNumber == null || nppfNumber.trim().isEmpty()) {
            throw new RuntimeException("NPPF number cannot be null or empty");
        }
        
        List<VisaDownloaded> entities = repository.findByNppfNumber(nppfNumber);
        log.info("Found {} visa downloads for NPPF number: {}", entities.size(), nppfNumber);
        
        // Return complete responses with nested DTOs
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisaDownloadResponse> getByCid(String cid) {
        log.info("Fetching visa downloads by CID: {}", cid);
        
        if (cid == null || cid.trim().isEmpty()) {
            throw new RuntimeException("CID cannot be null or empty");
        }
        
        List<VisaDownloaded> entities = repository.findByCid(cid);
        log.info("Found {} visa downloads for CID: {}", entities.size(), cid);
        
        // Return complete responses with nested DTOs
        return mapper.toResponseList(entities);
    }

    @Override
    public void deleteDownload(Long id) {
        log.info("Deleting visa download with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Visa download not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Visa download deleted successfully with ID: {}", id);
    }

    /**
     * Extract device name from User-Agent string
     * @param userAgent The User-Agent header
     * @return Extracted device name or "Unknown Device"
     */
    private String getDeviceNameFromUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }
        
        // Extract device information from User-Agent
        String deviceName = userAgent;
        
        // Check for common browsers/OS
        if (userAgent.contains("Windows")) {
            if (userAgent.contains("Windows NT 10.0")) {
                deviceName = "Windows 10/11 Device";
            } else if (userAgent.contains("Windows NT 6.3")) {
                deviceName = "Windows 8.1 Device";
            } else if (userAgent.contains("Windows NT 6.2")) {
                deviceName = "Windows 8 Device";
            } else if (userAgent.contains("Windows NT 6.1")) {
                deviceName = "Windows 7 Device";
            } else {
                deviceName = "Windows Device";
            }
        } else if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X")) {
            if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
                deviceName = "iOS Device";
            } else {
                deviceName = "Mac Device";
            }
        } else if (userAgent.contains("Android")) {
            deviceName = "Android Device";
        } else if (userAgent.contains("Linux")) {
            deviceName = "Linux Device";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            deviceName = "iOS Device";
        } else {
            // Try to get browser info
            if (userAgent.contains("Chrome")) {
                deviceName = "Chrome Browser";
            } else if (userAgent.contains("Firefox")) {
                deviceName = "Firefox Browser";
            } else if (userAgent.contains("Safari")) {
                deviceName = "Safari Browser";
            } else if (userAgent.contains("Edge")) {
                deviceName = "Edge Browser";
            } else {
                deviceName = userAgent.length() > 100 ? userAgent.substring(0, 100) + "..." : userAgent;
            }
        }
        
        return deviceName;
    }

    /**
     * Validates IP address format (IPv4)
     * @param ipAddress The IP address to validate
     * @throws RuntimeException if IP address is invalid
     */
    private void validateIpAddress(String ipAddress) {
        if (ipAddress != null && !ipAddress.isEmpty()) {
            // Check for IPv4
            if (!IPV4_PATTERN_COMPILED.matcher(ipAddress).matches()) {
                // Check for IPv6 (simplified check)
                if (!ipAddress.contains(":")) {
                    throw new RuntimeException("Invalid IP address format: " + ipAddress);
                }
                // IPv6 basic validation - can be enhanced
                if (ipAddress.split(":").length < 3) {
                    throw new RuntimeException("Invalid IP address format: " + ipAddress);
                }
            }
        }
    }
}