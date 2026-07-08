package com.claim.claim_processing.document.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.claim.claim_processing.document.dto.DocumentMasterPatchRequestDto;
import com.claim.claim_processing.document.dto.DocumentMasterRequestDto;
import com.claim.claim_processing.document.dto.DocumentMasterResponseDto;
import com.claim.claim_processing.document.dto.DocumentMasterUpdateDto;
import com.claim.claim_processing.document.service.DocumentMasterService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/claim-processing-flow/documents")
@RequiredArgsConstructor
@Slf4j
public class ClaimDocumentMasterController {

    private final DocumentMasterService documentMasterService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentMasterResponseDto> uploadDocuments(

            @Parameter(description = "Document upload request", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DocumentMasterRequestDto.class))) @RequestPart("request") DocumentMasterRequestDto request,

            @Parameter(description = "Files to upload", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "array", format = "binary"))) @RequestPart("files") List<MultipartFile> files) {
        log.info("Uploading documents for referenceId: {}", request.getReferenceId());

        DocumentMasterResponseDto response = documentMasterService.createDocument(request, files);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<DocumentMasterResponseDto> getByReferenceId(@PathVariable String referenceId) {
        log.info("Fetching documents by referenceId: {}", referenceId);

        DocumentMasterResponseDto response = documentMasterService.getByReferenceId(referenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFileById(@PathVariable Long id) throws IOException {
        Resource resource = documentMasterService.getFileById(id);

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PatchMapping(value = "/patch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentMasterResponseDto> patchDocument(

            @Parameter(description = "Document patch request list", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DocumentMasterUpdateDto.class))) @RequestPart("request") DocumentMasterPatchRequestDto request,

            @Parameter(description = "Files to patch or insert", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "array", format = "binary"))) @RequestPart("files") List<MultipartFile> files) {
        DocumentMasterResponseDto response = documentMasterService.patchDocument(request.getRequests(), files);
        return ResponseEntity.ok(response);
    }
}