package com.claim.claim_processing.application.repository.claimDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;

@Repository
public interface ClaimDetailRepository extends JpaRepository<ClaimDetail, Long> {

     @Query("SELECT cd FROM ClaimDetail cd ORDER BY cd.createdAt DESC")
    Page<ClaimDetail> findAllWithPagination(Pageable pageable);
    Optional<ClaimDetail> findByApplicationNumber(String applicationNumber);
}
