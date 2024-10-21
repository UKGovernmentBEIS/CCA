package uk.gov.cca.api.authorization.ccaauth.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaUserRoleType;

@Repository
public interface CcaUserRoleTypeRepository extends JpaRepository<CcaUserRoleType, String> {
}
