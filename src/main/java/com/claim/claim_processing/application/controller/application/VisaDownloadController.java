package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.DTO.request.others.VisaDownloadRequest;
import com.claim.claim_processing.application.DTO.response.others.VisaDownloadResponse;
import com.claim.claim_processing.application.service.others.VisaDownloadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visa/download")
@RequiredArgsConstructor
@Slf4j
public class VisaDownloadController {

    private final VisaDownloadService visaDownloadService;

    @PostMapping("/log")
    public ResponseEntity<VisaDownloadResponse> logDownload(
            @Valid @RequestBody VisaDownloadRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received visa download log request for channel ID: {}", request.getSubmissionChannelId());
        
        // Set IP address from request if not provided
        if (request.getIpAddress() == null || request.getIpAddress().isEmpty()) {
            request.setIpAddress(getClientIp(httpRequest));
            log.debug("IP address set from request: {}", request.getIpAddress());
        }
        
        // Get User-Agent for device name (backend will handle it)
        String userAgent = httpRequest.getHeader("User-Agent");
        log.debug("User-Agent: {}", userAgent);
        
        VisaDownloadResponse response = visaDownloadService.logDownload(request, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VisaDownloadResponse> updateDownload(
            @PathVariable Long id,
            @Valid @RequestBody VisaDownloadRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received visa download update request for ID: {}", id);
        
        // Get User-Agent for device name update (backend will handle it)
        String userAgent = httpRequest.getHeader("User-Agent");
        log.debug("User-Agent: {}", userAgent);
        
        VisaDownloadResponse response = visaDownloadService.updateDownload(id, request, userAgent);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nppf/{nppfNumber}")
    public ResponseEntity<List<VisaDownloadResponse>> getByNppfNumber(
            @PathVariable String nppfNumber) {
        
        log.info("Fetching visa downloads by NPPF number: {}", nppfNumber);
        List<VisaDownloadResponse> responses = visaDownloadService.getByNppfNumber(nppfNumber);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<VisaDownloadResponse>> getByCid(
            @PathVariable String cid) {
        
        log.info("Fetching visa downloads by CID: {}", cid);
        List<VisaDownloadResponse> responses = visaDownloadService.getByCid(cid);
        return ResponseEntity.ok(responses);
    }

    /**
     * Utility method to get client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs are present (X-Forwarded-For can have multiple), take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}