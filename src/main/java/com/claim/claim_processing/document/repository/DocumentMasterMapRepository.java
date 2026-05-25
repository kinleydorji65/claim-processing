package com.claim.claim_processing.document.repository;

import com.claim.claim_processing.document.entity.DocumentMasterMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentMasterMapRepository extends JpaRepository<DocumentMasterMap, Long> {

    Optional<DocumentMasterMap> findByDocument_IdAndClaimType_Id(
            Long documentId,
            Long claimTypeId
    );

    List<DocumentMasterMap> findByClaimType_Id(Long claimTypeId);

    List<DocumentMasterMap> findByDocument_Id(Long documentId);

    boolean existsByDocument_IdAndClaimType_Id(
            Long documentId,
            Long claimTypeId
    );

    void deleteByDocument_IdAndClaimType_Id(
            Long documentId,
            Long claimTypeId
    );
}
