package com.claim.claim_processing.document.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.document.entity.DocumentMaster;

public interface DocumentMasterRepository extends JpaRepository<DocumentMaster, Long> {
    List<DocumentMaster> findByReferenceId(String referenceId);
    List<DocumentMaster> findByServiceCode(String serviceCode);
    Optional<DocumentMaster> findFirstByReferenceIdAndServiceCodeAndFilePath(String referenceId, String serviceCode, String filePath);
    List<DocumentMaster> findByReferenceIdAndServiceCode(String referenceId, String serviceCode);
}
