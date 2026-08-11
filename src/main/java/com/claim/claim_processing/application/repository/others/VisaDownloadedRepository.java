package com.claim.claim_processing.application.repository.others;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.others.VisaDownloaded;

@Repository
public interface VisaDownloadedRepository extends JpaRepository<VisaDownloaded, Long> {
    // Empty repository - custom queries can be added here as needed

    List<VisaDownloaded> findByNppfNumber(String nppfNumber);
    List<VisaDownloaded> findByCid(String cid);
}
