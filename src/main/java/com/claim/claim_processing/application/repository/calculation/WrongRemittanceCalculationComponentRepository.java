package com.claim.claim_processing.application.repository.calculation;

import com.claim.claim_processing.application.entity.calculation.WrongRemittanceCalculationComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrongRemittanceCalculationComponentRepository extends JpaRepository<WrongRemittanceCalculationComponent, Long> {

}