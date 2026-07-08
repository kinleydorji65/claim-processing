package com.claim.claim_processing.document.service.impl;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.claim.claim_processing.document.dto.DocumentMasterRequestDto;
import com.claim.claim_processing.document.dto.DocumentMasterResponseDto;
import com.claim.claim_processing.document.dto.DocumentMasterUpdateDto;
import com.claim.claim_processing.document.entity.DocumentMaster;
import com.claim.claim_processing.document.mapper.DocumentMasterMapper;
import com.claim.claim_processing.document.repository.DocumentMasterRepository;
import com.claim.claim_processing.document.service.DocumentMasterService;
import com.claim.claim_processing.exceptions.ClaimException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentMasterServiceImpl implements DocumentMasterService {

    @Value("${file.application-dir}")
    private String applicationDir; // example: /opt/ppfms/application-details

    @Value("${file.approved-dir}")
    private String approvedDir; // example: /opt/ppfms/approved-details

    private final DocumentMasterRepository documentMasterRepository;
    private final DocumentMasterMapper documentMasterMapper;

    private Path resolveBaseDir(Boolean approved) {
        Path p = approved ? Paths.get(approvedDir) : Paths.get(applicationDir);
        return p.toAbsolutePath().normalize();
    }

    private String toUserFolderName(String userType) {
        if (userType == null) {
            return "unknown-documents";
        }

        String t = userType.trim().toUpperCase();

        return "claim-application";
    }

    /**
     * Store logical URL in DB, not physical OS path.
     * Example:
     * /approved-details/member-documents/PPFMS20260310M00227/file.png
     */
    private String buildFileUrl(Boolean isApproved, String safeUserFolder, String safeCode, String storedFileName) {
        String root = isApproved ? "/approved-claims/" : "/application-claims/";
        return root + safeUserFolder + "/" + safeCode + "/" + storedFileName;
    }

    /**
     * Convert logical DB file URL to physical filesystem path.
     *
     * Example:
     * /approved-details/member-documents/abc/file.png
     * -> /opt/ppfms/approved-details/member-documents/abc/file.png
     */
    private Path resolvePhysicalPathFromFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("fileUrl is blank");
        }

        String normalized = fileUrl.replace("\\", "/").trim();

        if (normalized.startsWith("/approved-details/")) {
            String relativePath = normalized.substring("/approved-details/".length()); // ✅ CORRECT
            Path basePath = Path.of(approvedDir);
            Path resolvedPath = basePath.resolve(relativePath).normalize();
            log.debug("Resolved approved file: {}", resolvedPath);

            // Security check
            if (!resolvedPath.startsWith(basePath)) {
                throw new IllegalArgumentException("Access denied: Path traversal detected");
            }
            return resolvedPath;
        }

        // Handle /application-details/ URLs
    if (normalized.startsWith("/application-details/")) {
        String relativePath = normalized.substring("/application-details/".length()); // ✅ CORRECT
        Path basePath = Path.of(applicationDir);
        Path resolvedPath = basePath.resolve(relativePath).normalize();
        log.debug("Resolved application file: {}", resolvedPath);
        
        // Security check
        if (!resolvedPath.startsWith(basePath)) {
            throw new IllegalArgumentException("Access denied: Path traversal detected");
        }
        return resolvedPath;
    }

        throw new IllegalArgumentException("Unsupported file url: " + fileUrl);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String sanitizePathSegment(String input) {
        if (input == null) {
            return "code";
        }
        String cleaned = input.trim();
        cleaned = cleaned.replace("..", "");
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "code" : cleaned;
    }

    private String sanitizeFileToken(String input) {
        if (input == null) {
            return "file";
        }
        String cleaned = input.replace("\\", "_").replace("/", "_");
        cleaned = cleaned.replace("..", "");
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "file" : cleaned;
    }

    private String joinNonEmpty(String sep, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p == null) {
                continue;
            }
            String t = p.trim();
            if (t.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(sep);
            }
            sb.append(t);
        }
        return sb.toString();
    }

    @Override
    public DocumentMasterResponseDto transferDocumentsForApproval(
            String oldReferenceId,
            String newReferenceId,
            String userType,
            String updatedBy) {
        try {
            if (isBlank(oldReferenceId)) {
                throw ClaimException.badRequest("oldReferenceId is required");
            }
            if (isBlank(newReferenceId)) {
                throw ClaimException.badRequest("newReferenceId is required");
            }
            if (isBlank(userType)) {
                throw ClaimException.badRequest("userType is required");
            }

            List<DocumentMaster> documents = documentMasterRepository.findByReferenceId(oldReferenceId);

            if (documents == null || documents.isEmpty()) {
                return null;
            }

            // if all docs are already in approved-claims, do not process
            boolean allAlreadyApproved = documents.stream()
                    .allMatch(doc -> doc.getFilePath() != null && doc.getFilePath().startsWith("/approved-details/"));

            if (allAlreadyApproved) {
                log.info("All documents for oldReferenceId {} are already in approved-details. Skipping transfer.",
                        oldReferenceId);
                return null;
            }

            LocalDateTime now = LocalDateTime.now();

            String safeUserFolder = sanitizePathSegment(toUserFolderName(userType));
            String safeCode = sanitizePathSegment(newReferenceId);

            Path newBaseDir = resolveBaseDir(true);
            Path newCodeDir = newBaseDir.resolve(safeUserFolder).resolve(safeCode).normalize();
            Files.createDirectories(newCodeDir);

            List<TransferItem> transferItems = new ArrayList<>();
            List<DocumentMaster> updatedDocs = new ArrayList<>();
            Path oldCodeDir = null;

            // Phase 1: validate everything first
            for (DocumentMaster entity : documents) {
                if (isBlank(entity.getFilePath())) {
                    throw ClaimException.notFound("File path is empty for document id: " + entity.getDocumentId());
                }

                // skip any individual file already in approved-details
                if (entity.getFilePath().startsWith("/approved-details/")) {
                    log.info("Document id {} already in approved-details, skipping. filePath={}",
                            entity.getDocumentId(), entity.getFilePath());
                    continue;
                }

                Path oldPath = resolvePhysicalPathFromFileUrl(entity.getFilePath());

                log.info("Document id: {}", entity.getDocumentId());
                log.info("DB filePath: {}", entity.getFilePath());
                log.info("Resolved source path: {}", oldPath);

                if (!Files.exists(oldPath)) {
                    throw ClaimException.notFound("Source file does not exist: " + oldPath);
                }

                if (oldCodeDir == null) {
                    oldCodeDir = oldPath.getParent();
                }

                String oldFileName = oldPath.getFileName().toString();
                String originalName = oldFileName;

                String oldPrefix = sanitizeFileToken(oldReferenceId) + "_";
                if (originalName.startsWith(oldPrefix)) {
                    originalName = originalName.substring(oldPrefix.length());
                }

                String fileType = "UNKNOWN";
                String originalFileNameWithoutExt = originalName;

                if (originalName.contains(".")) {
                    int dotIndex = originalName.lastIndexOf(".");
                    originalFileNameWithoutExt = originalName.substring(0, dotIndex);
                    fileType = originalName.substring(dotIndex + 1).toLowerCase();
                }

                String safeRefId = sanitizeFileToken(newReferenceId);
                String safeOriginal = sanitizeFileToken(originalName);

                String newStoredFileName = joinNonEmpty("_", safeRefId, safeOriginal);
                String newDocumentName = joinNonEmpty("_", safeRefId, originalFileNameWithoutExt);

                Path newTarget = newCodeDir.resolve(newStoredFileName).normalize();

                if (!newTarget.startsWith(newCodeDir)) {
                    throw ClaimException.badRequest("Invalid target file path after sanitization");
                }

                String newFileUrl = buildFileUrl(true, safeUserFolder, safeCode, newStoredFileName);

                transferItems.add(new TransferItem(
                        entity,
                        oldPath,
                        newTarget,
                        newFileUrl,
                        newDocumentName,
                        fileType));
            }

            // if after skipping approved files nothing remains, return null
            if (transferItems.isEmpty()) {
                log.info("No transferable documents found for oldReferenceId {}. Skipping transfer.", oldReferenceId);
                return null;
            }

            List<TransferItem> movedItems = new ArrayList<>();

            try {
                // Phase 2: move files
                for (TransferItem item : transferItems) {
                    Files.createDirectories(item.newTarget().getParent());
                    Files.move(item.oldPath(), item.newTarget(), StandardCopyOption.REPLACE_EXISTING);
                    movedItems.add(item);

                    DocumentMaster entity = item.entity();
                    entity.setReferenceId(newReferenceId);
                    entity.setDocumentName(item.newDocumentName());
                    entity.setFileType(item.fileType());
                    entity.setFilePath(item.newFileUrl());
                    entity.setUpdatedBy(updatedBy);
                    entity.setUpdatedAt(now);

                    updatedDocs.add(entity);
                }

                List<DocumentMaster> saved = documentMasterRepository.saveAll(updatedDocs);

                // cleanup old folder if empty
                if (oldCodeDir != null && Files.exists(oldCodeDir)) {
                    try (var stream = Files.list(oldCodeDir)) {
                        if (!stream.findAny().isPresent()) {
                            Files.delete(oldCodeDir);
                        }
                    }
                }

                return documentMasterMapper.toDto(saved);

            } catch (Exception ex) {
                // rollback moved files
                for (TransferItem item : movedItems) {
                    try {
                        if (Files.exists(item.newTarget())) {
                            Files.createDirectories(item.oldPath().getParent());
                            Files.move(item.newTarget(), item.oldPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception rollbackEx) {
                        log.error("Rollback failed for file: {}", item.newTarget(), rollbackEx);
                    }
                }
                throw ex;
            }

        } catch (ClaimException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ClaimException.internalError("Failed to transfer documents to approved folder", ex);
        } catch (Exception ex) {
            throw ClaimException.internalError("Error transferring documents for approval", ex);
        }
    }

    private record TransferItem(
            DocumentMaster entity,
            Path oldPath,
            Path newTarget,
            String newFileUrl,
            String newDocumentName,
            String fileType) {
    }

    // create document
    private final DocumentUploadAsyncService documentUploadAsyncService;

    @Override
    public DocumentMasterResponseDto createDocument(DocumentMasterRequestDto request, List<MultipartFile> files) {
        // validate(request, files);

        LocalDateTime now = LocalDateTime.now();

        // Get the base directory from configuration
        Path baseDir = Path.of(Boolean.TRUE.equals(request.getIsApproved()) ? approvedDir : applicationDir);
        log.info("Base directory resolved: {}", baseDir.toAbsolutePath());

        String safeUserFolder = sanitizePathSegment(toUserFolderName(request.getUserType()));
        String safeCode = sanitizePathSegment(request.getReferenceId());

        Path codeDir = baseDir.resolve(safeUserFolder).resolve(safeCode).normalize();
        log.info("Target directory: {}", codeDir.toAbsolutePath());

        List<Path> uploadedPaths = new ArrayList<>();

        try {
            // Create directories on the server
            Files.createDirectories(codeDir);
            log.info("Created directory: {}", codeDir.toAbsolutePath());

            List<CompletableFuture<DocumentUploadAsyncService.FileUploadResult>> futures = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                CompletableFuture<DocumentUploadAsyncService.FileUploadResult> future = documentUploadAsyncService
                        .processSingleFile(
                                file,
                                i,
                                request.getReferenceId(),
                                request.getServiceCode(),
                                request.getCreatedBy(),
                                request.getIsApproved(),
                                safeUserFolder,
                                safeCode,
                                codeDir,
                                now);

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<DocumentMaster> entitiesToSave = new ArrayList<>();

            for (CompletableFuture<DocumentUploadAsyncService.FileUploadResult> future : futures) {
                DocumentUploadAsyncService.FileUploadResult result = future.join();
                uploadedPaths.add(result.getStoredPath());
                entitiesToSave.add(result.getEntity());
            }

            List<DocumentMaster> saved = documentMasterRepository.saveAll(entitiesToSave);
            return documentMasterMapper.toDto(saved);

        } catch (Exception e) {
            // Clean up on failure
            for (Path path : uploadedPaths) {
                try {
                    Files.deleteIfExists(path);
                    log.info("Cleaned up file: {}", path);
                } catch (IOException ex) {
                    log.error("Failed to clean up uploaded file: {}", path, ex);
                }
            }
            log.error("Failed to upload files", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public DocumentMasterResponseDto getByReferenceId(String referenceId) {
        try {
            if (isBlank(referenceId)) {
                throw new IllegalArgumentException("referenceId is required");
            }

            List<DocumentMaster> documentMasters = documentMasterRepository.findByReferenceIdAndServiceCode(referenceId,
                    "105");

            if (documentMasters == null || documentMasters.isEmpty()) {
                throw new RuntimeException("Document not found for referenceId: " + referenceId);
            }

            return documentMasterMapper.toDto(documentMasters);

        } catch (Exception ex) {
            throw new RuntimeException("Error fetching document for referenceId: " + referenceId, ex);
        }
    }

    @Override
    public DocumentMasterResponseDto patchDocument(List<DocumentMasterUpdateDto> requests, List<MultipartFile> files) {
        List<Path> newUploadedPaths = new ArrayList<>();

        try {
            if (requests == null || requests.isEmpty()) {
                throw new IllegalArgumentException("Patch requests are required");
            }

            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException("At least one file is required");
            }

            if (requests.size() != files.size()) {
                throw new IllegalArgumentException("Requests count must match files count");
            }

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < requests.size(); i++) {
                DocumentMasterUpdateDto request = requests.get(i);
                MultipartFile file = files.get(i);

                if (request == null) {
                    throw new IllegalArgumentException("Request at index " + i + " is null");
                }

                if (file == null || file.isEmpty()) {
                    throw new IllegalArgumentException("Uploaded file at index " + i + " is empty");
                }

                if (isBlank(request.getUserType())) {
                    throw new IllegalArgumentException("userType is required at index " + i);
                }

                if (isBlank(request.getReferenceId())) {
                    throw new IllegalArgumentException("referenceId is required at index " + i);
                }

                long maxSize = 5 * 1024 * 1024;
                if (file.getSize() > maxSize) {
                    double sizeMB = file.getSize() / (1024.0 * 1024.0);
                    throw new IllegalArgumentException(
                            "File size exceeds 5MB. Current file size for file " + i + " is: "
                                    + String.format("%.2f", sizeMB) + " MB");
                }
            }

            List<CompletableFuture<DocumentUploadAsyncService.PatchFileResult>> futures = new ArrayList<>();

            for (int i = 0; i < requests.size(); i++) {
                DocumentMasterUpdateDto request = requests.get(i);
                MultipartFile file = files.get(i);

                Path baseDir = resolveBaseDir(Boolean.TRUE.equals(request.getIsApproved()));
                String safeUserFolder = sanitizePathSegment(toUserFolderName(request.getUserType()));
                String safeCode = sanitizePathSegment(request.getReferenceId());
                Path codeDir = baseDir.resolve(safeUserFolder).resolve(safeCode).normalize();

                Files.createDirectories(codeDir);

                CompletableFuture<DocumentUploadAsyncService.PatchFileResult> future = documentUploadAsyncService
                        .processPatchFile(
                                request,
                                file,
                                i,
                                safeUserFolder,
                                safeCode,
                                codeDir,
                                now);

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<DocumentMaster> entitiesToSave = new ArrayList<>();
            List<String> oldFileUrls = new ArrayList<>();

            for (CompletableFuture<DocumentUploadAsyncService.PatchFileResult> future : futures) {
                DocumentUploadAsyncService.PatchFileResult result = future.join();

                entitiesToSave.add(result.getEntity());

                if (result.getNewStoredPath() != null) {
                    newUploadedPaths.add(result.getNewStoredPath());
                }

                if (result.getOldFileUrl() != null && !result.getOldFileUrl().isBlank()) {
                    oldFileUrls.add(result.getOldFileUrl());
                }
            }

            List<DocumentMaster> saved = documentMasterRepository.saveAll(entitiesToSave);

            for (String oldFileUrl : oldFileUrls) {
                try {
                    Path oldPath = resolvePhysicalPathFromFileUrl(oldFileUrl);
                    if (oldPath != null) {
                        Files.deleteIfExists(oldPath);
                    }
                } catch (IOException e) {
                    log.error("Failed to delete old file: {}", oldFileUrl, e);
                }
            }

            return documentMasterMapper.toDto(saved);

        } catch (AccessDeniedException e) {
            cleanupUploadedFiles(newUploadedPaths);
            throw new RuntimeException("Permission denied while patching documents", e);

        } catch (IOException e) {
            cleanupUploadedFiles(newUploadedPaths);
            throw new RuntimeException("Failed to patch documents", e);

        } catch (Exception ex) {
            cleanupUploadedFiles(newUploadedPaths);
            throw new RuntimeException("Error patching/upserting documents", ex);
        }
    }

    private void cleanupUploadedFiles(List<Path> paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("Failed to clean up uploaded file: {}", path, e);
            }
        }
    }

    @Override
    public Resource getFileById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Document id is required");
        }

        DocumentMaster documentMaster = documentMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found for id: " + id));

        String storedPath = documentMaster.getFilePath();

        if (storedPath == null || storedPath.isBlank()) {
            throw new RuntimeException("File path is empty for document id: " + id);
        }

        try {
            Path filePath = resolvePhysicalPathFromFileUrl(storedPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + filePath);
            }

            return resource;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching file for id: " + id, ex);
        }
    }
}
