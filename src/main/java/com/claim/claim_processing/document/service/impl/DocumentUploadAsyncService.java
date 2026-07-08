package com.claim.claim_processing.document.service.impl;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.claim.claim_processing.document.dto.DocumentMasterUpdateDto;
import com.claim.claim_processing.document.entity.DocumentMaster;
import com.claim.claim_processing.document.repository.DocumentMasterRepository;

import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentUploadAsyncService {

    private final DocumentMasterRepository documentMasterRepository;

    @Async("fileUploadExecutor")
    public CompletableFuture<FileUploadResult> processSingleFile(
            MultipartFile file,
            int index,
            String referenceId,
            String serviceCode,
            String createdBy,
            Boolean isApproved,
            String safeUserFolder,
            String safeCode,
            Path codeDir,
            LocalDateTime now) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file at index " + index + " is empty");
            }

            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                double sizeMB = file.getSize() / (1024.0 * 1024.0);
                throw new IllegalArgumentException(
                        "File size exceeds 5MB. Current file size for file " + index + " is: "
                                + String.format("%.2f", sizeMB) + " MB");
            }

            String originalName = safeValue(file.getOriginalFilename(), "document_" + (index + 1));

            String fileType = "UNKNOWN";
            String originalFileNameWithoutExt = originalName;

            if (originalName.contains(".")) {
                int dotIndex = originalName.lastIndexOf(".");
                originalFileNameWithoutExt = originalName.substring(0, dotIndex);
                fileType = originalName.substring(dotIndex + 1).toLowerCase();
            }

            String safeRefId = sanitizeFileToken(safeValue(referenceId, "REF"));
            String safeOriginal = sanitizeFileToken(originalName);
            String uniqueToken = UUID.randomUUID().toString().replace("-", "");

            String storedFileName = joinNonEmpty("_", safeRefId, uniqueToken, safeOriginal);
            String docName = joinNonEmpty("_", safeRefId, originalFileNameWithoutExt);

            Path target = codeDir.resolve(storedFileName).normalize();

            if (!target.startsWith(codeDir)) {
                throw new IllegalArgumentException("Invalid file path after sanitization");
            }

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String fileUrl = buildFileUrl(isApproved, safeUserFolder, safeCode, storedFileName);

            DocumentMaster entity = DocumentMaster.builder()
                    .documentName(docName)
                    .fileType(fileType)
                    .filePath(fileUrl)
                    .referenceId(referenceId)
                    .serviceCode(serviceCode)
                    .createdBy(createdBy)
                    .createdAt(now)
                    .updatedBy(createdBy)
                    .updatedAt(now)
                    .build();

            log.info("Uploaded file {} on thread {}", storedFileName, Thread.currentThread().getName());

            return CompletableFuture.completedFuture(
                    FileUploadResult.builder()
                            .entity(entity)
                            .storedPath(target)
                            .build());

        } catch (AccessDeniedException e) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("Permission denied while saving file at index " + index, e));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("fileUploadExecutor")
    public CompletableFuture<PatchFileResult> processPatchFile(
            DocumentMasterUpdateDto request,
            MultipartFile file,
            int index,
            String safeUserFolder,
            String safeCode,
            Path codeDir,
            LocalDateTime now) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file at index " + index + " is empty");
            }

            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                double sizeMB = file.getSize() / (1024.0 * 1024.0);
                throw new IllegalArgumentException(
                        "File size exceeds 5MB. Current file size for file " + index + " is: "
                                + String.format("%.2f", sizeMB) + " MB");
            }

            String originalName = safeValue(file.getOriginalFilename(), "document_" + (index + 1));

            String fileType = "UNKNOWN";
            String originalFileNameWithoutExt = originalName;

            if (originalName.contains(".")) {
                int dotIndex = originalName.lastIndexOf(".");
                originalFileNameWithoutExt = originalName.substring(0, dotIndex);
                fileType = originalName.substring(dotIndex + 1).toLowerCase();
            }

            String safeRefId = sanitizeFileToken(safeValue(request.getReferenceId(), "REF"));
            String safeOriginal = sanitizeFileToken(originalName);
            String uniqueToken = UUID.randomUUID().toString().replace("-", "");

            // normalize full filename for stored file
            String normalizedSafeOriginal = safeOriginal.startsWith(safeRefId + "_")
                    ? safeOriginal.substring((safeRefId + "_").length())
                    : safeOriginal;

            // normalize filename without extension for document name
            String normalizedDocBase = originalFileNameWithoutExt.startsWith(safeRefId + "_")
                    ? originalFileNameWithoutExt.substring((safeRefId + "_").length())
                    : originalFileNameWithoutExt;

            String storedFileName = joinNonEmpty("_", safeRefId, uniqueToken, normalizedSafeOriginal);
            String docName = joinNonEmpty("_", safeRefId, normalizedDocBase);

            Path target = codeDir.resolve(storedFileName).normalize();

            if (!target.startsWith(codeDir)) {
                throw new IllegalArgumentException("Invalid file path after sanitization");
            }

            String fileUrl = buildFileUrl(request.getIsApproved(), safeUserFolder, safeCode, storedFileName);

            DocumentMaster entity;
            String oldFileUrl = null;

            if (request.getDocumentId() != null) {
                entity = documentMasterRepository.findById(request.getDocumentId())
                        .orElseThrow(() -> new RuntimeException(
                                "Document not found for id: " + request.getDocumentId()));

                if (entity.getReferenceId() != null
                        && request.getReferenceId() != null
                        && !entity.getReferenceId().equals(request.getReferenceId())) {
                    throw new RuntimeException("Document id " + request.getDocumentId()
                            + " does not belong to referenceId " + request.getReferenceId());
                }

                oldFileUrl = entity.getFilePath();

                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }

                entity.setDocumentName(docName);
                entity.setFileType(fileType);
                entity.setFilePath(fileUrl);
                entity.setReferenceId(request.getReferenceId());
                entity.setServiceCode(request.getServiceCode());
                entity.setUpdatedBy(request.getUpdatedBy());
                entity.setUpdatedAt(now);

                if (oldFileUrl != null && oldFileUrl.equals(fileUrl)) {
                    oldFileUrl = null;
                }

            } else {
                Optional<DocumentMaster> existingSameFile = documentMasterRepository
                        .findFirstByReferenceIdAndServiceCodeAndFilePath(
                                request.getReferenceId(),
                                request.getServiceCode(),
                                fileUrl);

                if (existingSameFile.isPresent()) {
                    entity = existingSameFile.get();

                    entity.setDocumentName(docName);
                    entity.setFileType(fileType);
                    entity.setFilePath(fileUrl);
                    entity.setReferenceId(request.getReferenceId());
                    entity.setServiceCode(request.getServiceCode());
                    entity.setUpdatedBy(request.getUpdatedBy());
                    entity.setUpdatedAt(now);

                } else {
                    try (InputStream in = file.getInputStream()) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }

                    entity = DocumentMaster.builder()
                            .documentName(docName)
                            .fileType(fileType)
                            .filePath(fileUrl)
                            .referenceId(request.getReferenceId())
                            .serviceCode(request.getServiceCode())
                            .createdBy(request.getUpdatedBy())
                            .createdAt(now)
                            .updatedBy(request.getUpdatedBy())
                            .updatedAt(now)
                            .build();
                }
            }

            log.info("Patched file {} on thread {}", storedFileName, Thread.currentThread().getName());

            return CompletableFuture.completedFuture(
                    PatchFileResult.builder()
                            .entity(entity)
                            .newStoredPath(target)
                            .oldFileUrl(oldFileUrl)
                            .build());

        } catch (AccessDeniedException e) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("Permission denied while patching file at index " + index, e));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Data
    @Builder
    public static class FileUploadResult {
        private DocumentMaster entity;
        private Path storedPath;
    }

    @Data
    @Builder
    public static class PatchFileResult {
        private DocumentMaster entity;
        private Path newStoredPath;
        private String oldFileUrl;
    }

    private String safeValue(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String sanitizeFileToken(String value) {
        return value == null ? "" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String joinNonEmpty(String delimiter, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part != null && !part.isBlank()) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(part);
            }
        }
        return sb.toString();
    }

    private String buildFileUrl(Boolean isApproved, String safeUserFolder, String safeCode, String storedFileName) {
        String rootFolder = Boolean.TRUE.equals(isApproved) ? "approved-details" : "application-details";
        return "/" + rootFolder + "/" + safeUserFolder + "/" + safeCode + "/" + storedFileName;
    }
}
