package uk.gov.cca.api.authorization.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.authorization.core.domain.UserRoleType;

/**
 * The UserRoleType Repository.
 */
@Repository
public interface UserRoleTypeRepository extends JpaRepository<UserRoleType, String> {
}
