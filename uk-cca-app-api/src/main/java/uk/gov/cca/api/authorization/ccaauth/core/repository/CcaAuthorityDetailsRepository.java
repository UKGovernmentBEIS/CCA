package uk.gov.cca.api.authorization.ccaauth.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;

@Repository
public interface CcaAuthorityDetailsRepository extends JpaRepository<CcaAuthorityDetails, Long> {

    @Transactional
    CcaAuthorityDetails findCcaAuthorityDetailsByAuthorityId(Long authorityId);
}
