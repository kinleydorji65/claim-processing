package com.claim.claim_processing.common.repository.common;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.claim.claim_processing.common.entities.others.UserRegistrateredAgencyMapping;
import com.claim.claim_processing.common.entities.others.UserRegistredAgencyMappingId;


public interface UserRegistrateredAgencyMappingRepository
        extends JpaRepository<UserRegistrateredAgencyMapping, UserRegistredAgencyMappingId> {

    List<UserRegistrateredAgencyMapping> findByUserCode(String userCode);

    List<UserRegistrateredAgencyMapping> findByAgencyCode(String agencyCode);

    Optional<UserRegistrateredAgencyMapping> findByUserCodeAndAgencyCode(
            String userCode,
            String agencyCode);
}
