package com.claim.claim_processing.application.service.others;

import com.claim.claim_processing.application.DTO.request.others.VisaDownloadRequest;
import com.claim.claim_processing.application.DTO.response.others.VisaDownloadResponse;

import java.util.List;

public interface VisaDownloadService {

    /**
     * Log a new visa download with complete nested data
     * @param request The download request DTO with nested data
     * @param userAgent The User-Agent header from HTTP request
     * @return The complete download response with nested DTOs
     */
    VisaDownloadResponse logDownload(VisaDownloadRequest request, String userAgent);

    /**
     * Update an existing visa download with complete nested data
     * @param id The ID of the download to update
     * @param request The update request DTO with nested data
     * @param userAgent The User-Agent header from HTTP request
     * @return The complete updated download response with nested DTOs
     */
    VisaDownloadResponse updateDownload(Long id, VisaDownloadRequest request, String userAgent);

    /**
     * Get visa downloads by NPPF number with complete nested data
     * @param nppfNumber The NPPF number to search for
     * @return List of complete download responses with nested DTOs
     */
    List<VisaDownloadResponse> getByNppfNumber(String nppfNumber);

    /**
     * Get visa downloads by CID with complete nested data
     * @param cid The CID to search for
     * @return List of complete download responses with nested DTOs
     */
    List<VisaDownloadResponse> getByCid(String cid);

    /**
     * Get visa download by ID with complete nested data
     * @param id The download ID
     * @return Complete download response with nested DTOs
     */
    VisaDownloadResponse getById(Long id);

    /**
     * Get all visa downloads with complete nested data
     * @return List of complete download responses with nested DTOs
     */
    List<VisaDownloadResponse> getAllDownloads();

    /**
     * Delete a visa download by ID
     * @param id The download ID to delete
     */
    void deleteDownload(Long id);
}