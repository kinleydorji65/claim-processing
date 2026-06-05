package com.claim.claim_processing.common.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.common.RentalMaster;

@Repository
public interface RentalMasterRepository extends JpaRepository<RentalMaster, Long> {

    boolean existsByRentalTypeIgnoreCase(String rentalType);
    RentalMaster findByRentalTypeIgnoreCase(String rentalType);

    boolean existsByRentalTypeIgnoreCaseAndIdNot(String rentalType, Long id);
}
